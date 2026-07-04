-- ========== 停车管理系统存储过程 ==========

-- (1) 车辆入场存储过程
DROP PROCEDURE IF EXISTS sp_vehicle_entry;
DELIMITER //

CREATE PROCEDURE sp_vehicle_entry(
    IN p_plate_number VARCHAR(20),
    IN p_user_id BIGINT,
    OUT p_space_number VARCHAR(20),
    OUT p_record_no VARCHAR(32)
)
BEGIN
    DECLARE v_space_id BIGINT;
    DECLARE v_record_no VARCHAR(32);

    -- 检查车辆是否已在场内
    IF EXISTS (SELECT 1 FROM parking_record
               WHERE plate_number = p_plate_number AND status = 'parking') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '车辆已在场内，请勿重复入场';
    END IF;

    -- 分配空闲车位
    SELECT id INTO v_space_id FROM parking_space
    WHERE status = 'free' AND is_enabled = 1 LIMIT 1;

    IF v_space_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '车位已满';
    END IF;

    -- 生成记录编号
    SET v_record_no = CONCAT('PR', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), FLOOR(RAND()*1000));

    -- 更新车位状态
    UPDATE parking_space SET status = 'occupied' WHERE id = v_space_id;

    -- 创建停车记录
    INSERT INTO parking_record (record_no, plate_number, user_id, space_id, enter_time, status)
    VALUES (v_record_no, p_plate_number, p_user_id, v_space_id, NOW(), 'parking');

    -- 返回结果
    SELECT space_number INTO p_space_number FROM parking_space WHERE id = v_space_id;
    SET p_record_no = v_record_no;

END //

DELIMITER ;


-- (2) 车辆出场计费存储过程
DROP PROCEDURE IF EXISTS sp_calculate_fee;
DELIMITER //

CREATE PROCEDURE sp_calculate_fee(
    IN p_record_id BIGINT,
    OUT p_original_fee DECIMAL(10,2),
    OUT p_discount_fee DECIMAL(10,2),
    OUT p_actual_fee DECIMAL(10,2)
)
BEGIN
    DECLARE v_enter_time DATETIME;
    DECLARE v_exit_time DATETIME;
    DECLARE v_minutes INT;
    DECLARE v_unit_price DECIMAL(10,2);
    DECLARE v_free_minutes INT;
    DECLARE v_cap_price DECIMAL(10,2);
    DECLARE v_is_holiday TINYINT;

    -- 获取入场时间和出场时间
    SELECT enter_time, IFNULL(exit_time, NOW()) INTO v_enter_time, v_exit_time
    FROM parking_record WHERE id = p_record_id;

    -- 计算停放分钟数
    SET v_minutes = TIMESTAMPDIFF(MINUTE, v_enter_time, v_exit_time);

    -- 判断是否为周末（周日=1，周六=7）
    SET v_is_holiday = IF(DAYOFWEEK(NOW()) IN (1, 7), 1, 0);

    -- 根据时间段和是否节假日获取计费规则
    IF v_is_holiday = 1 THEN
        SELECT unit_price, free_minutes, cap_price
        INTO v_unit_price, v_free_minutes, v_cap_price
        FROM charge_rule WHERE rule_type = 'holiday' AND is_active = 1 LIMIT 1;
    ELSE
        IF CURTIME() BETWEEN '08:00:00' AND '18:00:00' THEN
            SELECT unit_price, free_minutes, cap_price
            INTO v_unit_price, v_free_minutes, v_cap_price
            FROM charge_rule WHERE rule_type = 'normal' AND is_active = 1 ORDER BY id LIMIT 1;
        ELSE
            SELECT unit_price, free_minutes, cap_price
            INTO v_unit_price, v_free_minutes, v_cap_price
            FROM charge_rule WHERE rule_type = 'normal' AND is_active = 1 ORDER BY id DESC LIMIT 1;
        END IF;
    END IF;

    -- 若未查到规则，使用默认值
    IF v_unit_price IS NULL THEN
        SET v_unit_price = 5.00;
        SET v_free_minutes = 15;
        SET v_cap_price = 50.00;
    END IF;

    -- 计算原始费用
    IF v_minutes <= v_free_minutes THEN
        SET p_original_fee = 0;
    ELSE
        SET p_original_fee = CEIL((v_minutes - v_free_minutes) / 60.0) * v_unit_price;
    END IF;

    -- 应用封顶
    IF p_original_fee > v_cap_price AND v_cap_price > 0 THEN
        SET p_original_fee = v_cap_price;
    END IF;

    -- 初始无优惠
    SET p_discount_fee = 0;
    SET p_actual_fee = p_original_fee;

END //

DELIMITER ;


-- (3) 车辆出场结算存储过程
DROP PROCEDURE IF EXISTS sp_vehicle_exit;
DELIMITER //

CREATE PROCEDURE sp_vehicle_exit(
    IN p_record_id BIGINT,
    IN p_payment_method VARCHAR(20),
    OUT p_order_no VARCHAR(32),
    OUT p_payment_no VARCHAR(32),
    OUT p_amount DECIMAL(10,2)
)
BEGIN
    DECLARE v_plate_number VARCHAR(20);
    DECLARE v_space_id BIGINT;
    DECLARE v_original_fee DECIMAL(10,2);
    DECLARE v_discount_fee DECIMAL(10,2);
    DECLARE v_actual_fee DECIMAL(10,2);
    DECLARE v_order_no VARCHAR(32);
    DECLARE v_payment_no VARCHAR(32);
    DECLARE v_user_id BIGINT;
    DECLARE v_order_id BIGINT;

    -- 获取停车记录信息
    SELECT plate_number, space_id, user_id
    INTO v_plate_number, v_space_id, v_user_id
    FROM parking_record WHERE id = p_record_id AND status = 'parking';

    IF v_plate_number IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '未找到该停车记录或车辆已离场';
    END IF;

    -- 计算费用
    CALL sp_calculate_fee(p_record_id, v_original_fee, v_discount_fee, v_actual_fee);

    -- 生成订单号
    SET v_order_no = CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), FLOOR(RAND()*1000));
    SET v_payment_no = CONCAT('PAY', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), FLOOR(RAND()*1000));

    -- 插入订单
    INSERT INTO `order` (order_no, plate_number, user_id, total_amount, paid_amount, status, create_time)
    VALUES (v_order_no, v_plate_number, v_user_id, v_original_fee, v_actual_fee, 'completed', NOW());

    SET v_order_id = LAST_INSERT_ID();

    -- 插入支付记录
    INSERT INTO payment_record (payment_no, record_id, amount, payment_method, status, payment_time)
    VALUES (v_payment_no, p_record_id, v_actual_fee, p_payment_method, 'success', NOW());

    -- 插入财务记录
    INSERT INTO financial_record (order_id, amount, payment_method, record_type, record_time)
    VALUES (v_order_id, v_actual_fee, p_payment_method, 'income', NOW());

    -- 更新停车记录
    UPDATE parking_record
    SET exit_time = NOW(),
        duration_minutes = TIMESTAMPDIFF(MINUTE, enter_time, NOW()),
        original_fee = v_original_fee,
        discount_fee = v_discount_fee,
        actual_fee = v_actual_fee,
        status = 'completed',
        payment_status = 'paid'
    WHERE id = p_record_id;

    -- 释放车位
    UPDATE parking_space SET status = 'free' WHERE id = v_space_id;

    -- 返回结果
    SET p_order_no = v_order_no;
    SET p_payment_no = v_payment_no;
    SET p_amount = v_actual_fee;

END //

DELIMITER ;
