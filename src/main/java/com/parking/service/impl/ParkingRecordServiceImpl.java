package com.parking.service.impl;

import com.parking.entity.*;
import com.parking.mapper.ParkingRecordMapper;
import com.parking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingRecordServiceImpl implements IParkingRecordService {

    @Autowired
    private ParkingRecordMapper parkingRecordMapper;

    @Autowired
    private IChargeRuleService chargeRuleService;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMemberLevelService memberLevelService;

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
        ParkingRecord record = new ParkingRecord();
        record.setRecordNo("PR" + System.currentTimeMillis());
        record.setPlateNumber(plateNumber);
        record.setUserId(userId);
        record.setSpaceId(spaceId);
        record.setEnterTime(LocalDateTime.now());
        record.setStatus("parking");
        record.setPaymentStatus("unpaid");
        parkingRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void exit(Long recordId, String paymentMethod) {
        ParkingRecord record = parkingRecordMapper.selectById(recordId);
        if (record == null || !"parking".equals(record.getStatus())) {
            throw new RuntimeException("记录不存在或已离场");
        }

        long minutes = java.time.Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes();
        int durationMinutes = (int) minutes;
        record.setExitTime(LocalDateTime.now());
        record.setDurationMinutes(durationMinutes);

        // 获取计费规则
        List<ChargeRule> rules = chargeRuleService.listActive();
        ChargeRule rule = (rules != null && !rules.isEmpty()) ? rules.get(0) : null;

        // 系统配置覆盖
        Integer freeMinutes = getConfigInt("free_parking_minutes", rule != null ? rule.getFreeMinutes() : 15);
        BigDecimal unitPrice = getConfigDecimal("unit_price", rule != null ? rule.getUnitPrice() : new BigDecimal("5"));
        BigDecimal capPrice = getConfigDecimal("cap_amount", rule != null ? rule.getCapPrice() : null);

        // 计算费用
        int chargeableMinutes = Math.max(0, durationMinutes - freeMinutes);
        int hours = (int) Math.ceil(chargeableMinutes / 60.0);
        BigDecimal originalFee = unitPrice.multiply(BigDecimal.valueOf(hours));
        if (capPrice != null && originalFee.compareTo(capPrice) > 0) {
            originalFee = capPrice;
        }

        // 会员折扣
        BigDecimal discountFee = BigDecimal.ZERO;
        Long userId = record.getUserId();
        if (userId != null) {
            User user = userService.getUser(userId);
            if (user != null && user.getMemberLevelId() != null) {
                MemberLevel level = memberLevelService.getLevel(user.getMemberLevelId());
                if (level != null && level.getDiscountRate() != null) {
                    BigDecimal rate = level.getDiscountRate();
                    discountFee = originalFee.multiply(BigDecimal.ONE.subtract(rate)).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }

        BigDecimal actualFee = originalFee.subtract(discountFee).max(BigDecimal.ZERO);

        record.setOriginalFee(originalFee.setScale(2, RoundingMode.HALF_UP));
        record.setDiscountFee(discountFee);
        record.setActualFee(actualFee.setScale(2, RoundingMode.HALF_UP));
        record.setStatus("completed");
        record.setPaymentStatus(paymentMethod != null && !paymentMethod.isEmpty() ? "paid" : "unpaid");
        parkingRecordMapper.update(record);
    }

    private Integer getConfigInt(String key, Integer defaultValue) {
        SystemConfig config = systemConfigService.getConfigByKey(key);
        if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    private BigDecimal getConfigDecimal(String key, BigDecimal defaultValue) {
        SystemConfig config = systemConfigService.getConfigByKey(key);
        if (config != null && config.getConfigValue() != null && !config.getConfigValue().isEmpty()) {
            try {
                return new BigDecimal(config.getConfigValue());
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }
}
