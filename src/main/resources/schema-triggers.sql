-- ========== 停车管理系统触发器 ==========

-- (1) 出场后自动释放车位触发器
-- 当停车记录状态更新为"已完成"时，自动将对应的车位状态恢复为"空闲"
DROP TRIGGER IF EXISTS trg_release_space;
DELIMITER //

CREATE TRIGGER trg_release_space
AFTER UPDATE ON parking_record
FOR EACH ROW
BEGIN
    IF NEW.status = 'completed' AND NEW.exit_time IS NOT NULL THEN
        UPDATE parking_space SET status = 'free' WHERE id = NEW.space_id;
    END IF;
END //

DELIMITER ;


-- (2) 月租到期自动移出白名单触发器
-- 当月租车辆状态更新为"已过期"时，自动将其从白名单中移除
DROP TRIGGER IF EXISTS trg_remove_whitelist;
DELIMITER //

CREATE TRIGGER trg_remove_whitelist
AFTER UPDATE ON monthly_vehicle
FOR EACH ROW
BEGIN
    IF NEW.status = 'expired' THEN
        UPDATE white_list SET status = 'inactive'
        WHERE plate_number = NEW.plate_number AND status = 'active';
    END IF;
END //

DELIMITER ;


-- (3) 车位状态变化时自动更新区域统计触发器
-- 当车位状态或启用状态发生变化时，自动更新对应区域的车位总数
DROP TRIGGER IF EXISTS trg_update_area_stats;
DELIMITER //

CREATE TRIGGER trg_update_area_stats
AFTER UPDATE ON parking_space
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status OR OLD.is_enabled != NEW.is_enabled THEN
        UPDATE parking_area SET total_spaces = (
            SELECT COUNT(*) FROM parking_space
            WHERE area_id = NEW.area_id AND is_enabled = 1
        ) WHERE id = NEW.area_id;
    END IF;
END //

DELIMITER ;


-- (4) 生成订单时自动创建财务记录触发器
-- 当插入新订单时，自动在财务记录表中生成对应的财务记录，确保财务数据完整
DROP TRIGGER IF EXISTS trg_auto_financial_record;
DELIMITER //

CREATE TRIGGER trg_auto_financial_record
AFTER INSERT ON `order`
FOR EACH ROW
BEGIN
    DECLARE v_payment_method VARCHAR(20);

    -- 从该车牌最近一次支付记录中获取支付方式
    SELECT pr.payment_method INTO v_payment_method
    FROM payment_record pr
    JOIN parking_record pk ON pr.record_id = pk.id
    WHERE pk.plate_number = NEW.plate_number
    ORDER BY pr.payment_time DESC LIMIT 1;

    IF v_payment_method IS NULL THEN
        SET v_payment_method = 'unknown';
    END IF;

    -- 避免与 Java 层重复插入
    IF NOT EXISTS (SELECT 1 FROM financial_record WHERE order_id = NEW.id) THEN
        INSERT INTO financial_record (order_id, amount, payment_method, record_type, record_time)
        VALUES (NEW.id, NEW.paid_amount, v_payment_method, 'income', NOW());
    END IF;
END //

DELIMITER ;
