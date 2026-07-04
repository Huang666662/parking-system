package com.parking.service.impl;

import com.parking.entity.*;
import com.parking.mapper.ParkingRecordMapper;
import com.parking.mapper.OrdersMapper;
import com.parking.mapper.UserMapper;
import com.parking.mapper.ParkingSpaceMapper;
import com.parking.mapper.VehicleProcedureMapper;
import com.parking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingRecordServiceImpl implements IParkingRecordService {

    @Autowired
    private ParkingRecordMapper parkingRecordMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VehicleProcedureMapper vehicleProcedureMapper;

    @Autowired
    private ParkingSpaceMapper parkingSpaceMapper;

    @Autowired
    private IChargeRuleService chargeRuleService;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMemberLevelService memberLevelService;

    @Autowired
    private IPaymentRecordService paymentRecordService;

    @Autowired
    private IFinancialRecordService financialRecordService;

    @Override
    public int addRecord(ParkingRecord record) {
        return parkingRecordMapper.insert(record);
    }

    @Override
    public int updateRecord(ParkingRecord record) {
        return parkingRecordMapper.update(record);
    }

    @Override
    public ParkingRecord getRecord(Long id) {
        return parkingRecordMapper.selectById(id);
    }

    @Override
    public ParkingRecord getCurrentByPlate(String plateNumber) {
        return parkingRecordMapper.selectCurrentByPlate(plateNumber);
    }

    @Override
    public List<ParkingRecord> listRecords(String plateNumber) {
        if (plateNumber != null && !plateNumber.isEmpty()) {
            return parkingRecordMapper.selectByPlate(plateNumber);
        }
        return parkingRecordMapper.selectAll();
    }

    @Override
    @Transactional
    public void enter(String plateNumber, Long userId, Long spaceId) {
        // 使用存储过程 sp_vehicle_entry（自动分配车位、防重复入场）
        Map<String, Object> params = new HashMap<>();
        params.put("plateNumber", plateNumber);
        params.put("userId", userId);
        params.put("spaceNumber", null);
        params.put("recordNo", null);
        vehicleProcedureMapper.callVehicleEntry(params);
    }

    @Override
    @Transactional
    public void exit(Long recordId, String paymentMethod) {
        ParkingRecord record = parkingRecordMapper.selectById(recordId);
        if (record == null || !"parking".equals(record.getStatus())) {
            throw new RuntimeException("记录不存在或已离场");
        }

        // 优先使用存储过程 sp_calculate_fee 计算费用，失败则 Java 兜底
        BigDecimal originalFee = null;
        BigDecimal discountFee = null;
        BigDecimal actualFee = null;
        try {
            Map<String, Object> feeParams = new HashMap<>();
            feeParams.put("recordId", recordId);
            feeParams.put("originalFee", null);
            feeParams.put("discountFee", null);
            feeParams.put("actualFee", null);
            vehicleProcedureMapper.callCalculateFee(feeParams);
            originalFee = (BigDecimal) feeParams.get("originalFee");
            discountFee = (BigDecimal) feeParams.get("discountFee");
        } catch (Exception ignored) {
            // 存储过程不可用时跳过，后续由 Java 计算
        }

        if (originalFee == null) {
            // Java 兜底计费
            long minutes = java.time.Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes();
            List<ChargeRule> rules = chargeRuleService.listActive();
            ChargeRule rule = (rules != null && !rules.isEmpty()) ? rules.get(0) : null;

            Integer freeMinutes = getConfigInt("free_parking_minutes", rule != null ? rule.getFreeMinutes() : 15);
            BigDecimal unitPrice = getConfigDecimal("unit_price", rule != null ? rule.getUnitPrice() : new BigDecimal("5"));
            BigDecimal capPrice = getConfigDecimal("cap_amount", rule != null ? rule.getCapPrice() : null);

            int chargeableMinutes = Math.max(0, (int) minutes - freeMinutes);
            int hours = (int) Math.ceil(chargeableMinutes / 60.0);
            originalFee = unitPrice.multiply(BigDecimal.valueOf(hours));
            if (capPrice != null && originalFee.compareTo(capPrice) > 0) {
                originalFee = capPrice;
            }
            originalFee = originalFee.setScale(2, RoundingMode.HALF_UP);
            discountFee = BigDecimal.ZERO;
        }

        // 应用会员折扣
        Long userId = record.getUserId();
        User user = null;
        if (userId != null) {
            user = userService.getUser(userId);
            if (user != null && user.getMemberLevelId() != null) {
                MemberLevel level = memberLevelService.getLevel(user.getMemberLevelId());
                if (level != null && level.getDiscountRate() != null) {
                    BigDecimal rate = level.getDiscountRate();
                    BigDecimal memberDiscount = originalFee.multiply(BigDecimal.ONE.subtract(rate)).setScale(2, RoundingMode.HALF_UP);
                    discountFee = discountFee.add(memberDiscount);
                }
            }
        }
        actualFee = originalFee.subtract(discountFee).max(BigDecimal.ZERO);
        actualFee = actualFee.setScale(2, RoundingMode.HALF_UP);

        // 更新停车记录
        long minutes = java.time.Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes();
        record.setExitTime(LocalDateTime.now());
        record.setDurationMinutes((int) minutes);
        record.setOriginalFee(originalFee.setScale(2, RoundingMode.HALF_UP));
        record.setDiscountFee(discountFee);
        record.setActualFee(actualFee);

        boolean isPaid = paymentMethod != null && !paymentMethod.isEmpty();
        record.setStatus("completed");
        record.setPaymentStatus(isPaid ? "paid" : "unpaid");
        parkingRecordMapper.update(record);

        // 释放车位
        if (record.getSpaceId() != null) {
            try {
                ParkingSpace space = parkingSpaceMapper.selectById(record.getSpaceId());
                if (space != null) {
                    space.setStatus("free");
                    parkingSpaceMapper.update(space);
                }
            } catch (Exception ignored) {}
        }

        // 创建订单
        Orders order = new Orders();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setPlateNumber(record.getPlateNumber());
        order.setUserId(record.getUserId());
        order.setTotalAmount(actualFee);
        order.setPaidAmount(isPaid ? actualFee : BigDecimal.ZERO);
        order.setStatus(isPaid ? "paid" : "unpaid");
        ordersMapper.insert(order);

        // 已支付则创建支付记录和财务记录
        if (isPaid) {
            PaymentRecord payment = new PaymentRecord();
            payment.setPaymentNo("PAY" + System.currentTimeMillis());
            payment.setRecordId(record.getId());
            payment.setAmount(actualFee);
            payment.setPaymentMethod(paymentMethod);
            payment.setDiscountPoints(0);
            payment.setStatus("success");
            paymentRecordService.addPayment(payment);

            FinancialRecord financial = new FinancialRecord();
            financial.setOrderId(order.getId());
            financial.setAmount(actualFee);
            financial.setPaymentMethod(paymentMethod);
            financial.setRecordType("income");
            financialRecordService.addFinancialRecord(financial);

            // 积分奖励：每消费1元得1积分
            if (user != null) {
                int earnedPoints = actualFee.intValue();
                if (earnedPoints > 0) {
                    user.setPoints((user.getPoints() != null ? user.getPoints() : 0) + earnedPoints);
                    userService.updateUser(user);
                }
            }
        }
    }

    private Integer getConfigInt(String key, Integer defaultValue) {
        try {
            SystemConfig config = systemConfigService.getConfigByKey(key);
            if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
                try { return Integer.parseInt(config.getConfigValue()); } catch (NumberFormatException ignored) {}
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    private BigDecimal getConfigDecimal(String key, BigDecimal defaultValue) {
        try {
            SystemConfig config = systemConfigService.getConfigByKey(key);
            if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
                try { return new BigDecimal(config.getConfigValue()); } catch (NumberFormatException ignored) {}
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }
}
