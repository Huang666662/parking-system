package com.parking.service.impl;

import com.parking.entity.*;
import com.parking.mapper.ParkingRecordMapper;
import com.parking.mapper.OrdersMapper;
import com.parking.mapper.UserMapper;
import com.parking.mapper.ParkingSpaceMapper;
import com.parking.mapper.VehicleProcedureMapper;
import com.parking.mapper.PointsRecordMapper;
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

    @Autowired
    private ICouponService couponService;

    @Autowired
    private IUserCouponService userCouponService;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

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
        Map<String, Object> params = new HashMap<>();
        params.put("plateNumber", plateNumber);
        params.put("userId", userId);
        params.put("spaceNumber", null);
        params.put("recordNo", null);
        vehicleProcedureMapper.callVehicleEntry(params);
    }

    @Override
    @Transactional
    public void exit(Long recordId, String paymentMethod, Integer usePoints, Long userCouponId) {
        ParkingRecord record = parkingRecordMapper.selectById(recordId);
        if (record == null || !"parking".equals(record.getStatus())) {
            throw new RuntimeException("记录不存在或已离场");
        }

        long minutes = java.time.Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes();
        Long userId = record.getUserId();
        boolean hasDiscounts = (usePoints != null && usePoints > 0) || userCouponId != null;
        boolean isPaid = paymentMethod != null && !paymentMethod.isEmpty();

        // ========== 路径1：无优惠券/积分时，优先使用存储过程 sp_vehicle_exit ==========
        if (!hasDiscounts && isPaid) {
            try {
                Map<String, Object> exitParams = new HashMap<>();
                exitParams.put("recordId", recordId);
                exitParams.put("paymentMethod", paymentMethod);
                exitParams.put("orderNo", null);
                exitParams.put("paymentNo", null);
                exitParams.put("amount", null);
                vehicleProcedureMapper.callVehicleExit(exitParams);

                // 存储过程成功后，积分奖励
                BigDecimal spAmount = (BigDecimal) exitParams.get("amount");
                if (spAmount != null && userId != null) {
                    User user = userService.getUser(userId);
                    if (user != null) {
                        int earnedPoints = spAmount.intValue();
                        if (earnedPoints > 0) {
                            user.setPoints((user.getPoints() != null ? user.getPoints() : 0) + earnedPoints);
                            userService.updateUser(user);

                            PointsRecord earnRecord = new PointsRecord();
                            earnRecord.setUserId(user.getId());
                            earnRecord.setPoints(earnedPoints);
                            earnRecord.setType("收入");
                            earnRecord.setDescription("停车消费奖励 +" + earnedPoints + "积分");
                            pointsRecordMapper.insert(earnRecord);
                        }
                    }
                }
                return;
            } catch (Exception ignored) {
                // 存储过程失败，走 Java 兜底
            }
        }

        // ========== 路径2：Java 完整计费（存储过程不可用 或 有优惠券/积分折扣） ==========

        // 第1步：计算原始费用（优先 sp_calculate_fee）
        BigDecimal originalFee = null;
        try {
            Map<String, Object> feeParams = new HashMap<>();
            feeParams.put("recordId", recordId);
            feeParams.put("originalFee", null);
            feeParams.put("discountFee", null);
            feeParams.put("actualFee", null);
            vehicleProcedureMapper.callCalculateFee(feeParams);
            originalFee = (BigDecimal) feeParams.get("originalFee");
        } catch (Exception ignored) {
        }

        if (originalFee == null) {
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
        }
        originalFee = originalFee.setScale(2, RoundingMode.HALF_UP);
        if (originalFee.compareTo(BigDecimal.ZERO) < 0) {
            originalFee = BigDecimal.ZERO;
        }

        BigDecimal currentFee = originalFee;
        BigDecimal memberDiscountAmount = BigDecimal.ZERO;
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        BigDecimal pointsDiscountAmount = BigDecimal.ZERO;
        int actualUsePoints = 0;
        Long actualCouponId = null;

        // 加载用户信息
        User user = null;
        MemberLevel level = null;
        if (userId != null) {
            user = userService.getUser(userId);
            if (user != null && user.getMemberLevelId() != null) {
                level = memberLevelService.getLevel(user.getMemberLevelId());
            }
        }

        // 第2步：会员等级折扣
        if (level != null && level.getDiscountRate() != null
                && level.getDiscountRate().compareTo(BigDecimal.ONE) < 0) {
            BigDecimal afterMemberDiscount = originalFee.multiply(level.getDiscountRate())
                    .setScale(2, RoundingMode.HALF_UP);
            memberDiscountAmount = originalFee.subtract(afterMemberDiscount);
            currentFee = afterMemberDiscount;
        }

        // 第3步：优惠券抵扣
        if (userCouponId != null && currentFee.compareTo(BigDecimal.ZERO) > 0) {
            UserCoupon userCoupon = userCouponService.getUserCoupon(userCouponId);
            if (userCoupon != null
                    && "unused".equals(userCoupon.getStatus())
                    && userCoupon.getUserId().equals(userId)) {
                Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());
                if (coupon != null && coupon.getStatus() != null && coupon.getStatus() == 1
                        && (coupon.getEndTime() == null || coupon.getEndTime().isAfter(LocalDateTime.now()))) {
                    BigDecimal minAmount = coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO;
                    if (currentFee.compareTo(minAmount) >= 0) {
                        if ("amount".equals(coupon.getCouponType())) {
                            BigDecimal discountValue = coupon.getDiscountValue() != null
                                    ? coupon.getDiscountValue() : BigDecimal.ZERO;
                            couponDiscountAmount = discountValue.min(currentFee);
                            currentFee = currentFee.subtract(couponDiscountAmount);
                        } else {
                            BigDecimal discountRate = coupon.getDiscountValue() != null
                                    ? coupon.getDiscountValue() : BigDecimal.ONE;
                            BigDecimal afterCoupon = currentFee.multiply(discountRate)
                                    .setScale(2, RoundingMode.HALF_UP);
                            couponDiscountAmount = currentFee.subtract(afterCoupon);
                            currentFee = afterCoupon;
                        }
                        actualCouponId = coupon.getId();
                        userCouponService.markUsed(userCouponId);
                    }
                }
            }
        }

        // 第4步：积分抵扣
        if (usePoints != null && usePoints > 0 && user != null && currentFee.compareTo(BigDecimal.ZERO) > 0) {
            int userPoints = user.getPoints() != null ? user.getPoints() : 0;
            actualUsePoints = Math.min(usePoints, userPoints);
            if (actualUsePoints > 0) {
                int exchangeRate = getConfigInt("points_exchange_rate", 100);
                BigDecimal pointsToYuan = new BigDecimal(actualUsePoints)
                        .divide(new BigDecimal(exchangeRate), 2, RoundingMode.HALF_UP);
                pointsDiscountAmount = pointsToYuan.min(currentFee);
                currentFee = currentFee.subtract(pointsDiscountAmount);

                user.setPoints(userPoints - actualUsePoints);
                userService.updateUser(user);

                PointsRecord pointsRecord = new PointsRecord();
                pointsRecord.setUserId(user.getId());
                pointsRecord.setPoints(-actualUsePoints);
                pointsRecord.setType("支出");
                pointsRecord.setDescription("停车费抵扣 -" + actualUsePoints + "积分");
                pointsRecordMapper.insert(pointsRecord);
            }
        }

        // 第5步：最终金额
        BigDecimal actualFee = currentFee.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDiscount = originalFee.subtract(actualFee).setScale(2, RoundingMode.HALF_UP);

        // 更新停车记录
        record.setExitTime(LocalDateTime.now());
        record.setDurationMinutes((int) minutes);
        record.setOriginalFee(originalFee);
        record.setDiscountFee(totalDiscount);
        record.setActualFee(actualFee);
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
            payment.setDiscountPoints(actualUsePoints);
            payment.setCouponId(actualCouponId);
            payment.setStatus("success");
            paymentRecordService.addPayment(payment);

            FinancialRecord financial = new FinancialRecord();
            financial.setOrderId(order.getId());
            financial.setAmount(actualFee);
            financial.setPaymentMethod(paymentMethod);
            financial.setRecordType("income");
            financialRecordService.addFinancialRecord(financial);

            // 积分奖励
            if (user != null) {
                int earnedPoints = actualFee.intValue();
                if (earnedPoints > 0) {
                    User freshUser = userService.getUser(user.getId());
                    freshUser.setPoints((freshUser.getPoints() != null ? freshUser.getPoints() : 0) + earnedPoints);
                    userService.updateUser(freshUser);

                    PointsRecord earnRecord = new PointsRecord();
                    earnRecord.setUserId(freshUser.getId());
                    earnRecord.setPoints(earnedPoints);
                    earnRecord.setType("收入");
                    earnRecord.setDescription("停车消费奖励 +" + earnedPoints + "积分");
                    pointsRecordMapper.insert(earnRecord);
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
