package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.*;
import com.parking.service.*;
import com.parking.util.OperationLogHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/parking-record")
public class ParkingRecordController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private IParkingRecordService parkingRecordService;

    @Autowired
    private IChargeRuleService chargeRuleService;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMemberLevelService memberLevelService;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private IUserCouponService userCouponService;

    @Autowired
    private OperationLogHelper operationLogHelper;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String plateNumber,
                       Model model, HttpSession session) {
        String userType = (String) session.getAttribute("userType");
        Long userId = null;
        if ("user".equals(userType)) {
            User user = (User) session.getAttribute("user");
            if (user != null) userId = user.getId();
        }
        PageHelper.startPage(page, size);
        List<ParkingRecord> list = parkingRecordService.listRecords(plateNumber);
        if ("user".equals(userType) && userId != null) {
            final Long currentUserId = userId;
            list.removeIf(r -> !currentUserId.equals(r.getUserId()));
        }

        List<Map<String, Object>> recordList = new ArrayList<>();
        for (ParkingRecord r : list) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", r.getId());
            entry.put("recordNo", r.getRecordNo());
            entry.put("plateNumber", r.getPlateNumber());
            entry.put("enterTimeStr", r.getEnterTime() != null ? r.getEnterTime().format(FMT) : "");
            entry.put("exitTimeStr", r.getExitTime() != null ? r.getExitTime().format(FMT) : "");
            entry.put("durationMinutes", r.getDurationMinutes());
            entry.put("actualFee", r.getActualFee());
            entry.put("status", r.getStatus());
            entry.put("statusLabel", "parking".equals(r.getStatus()) ? "停放中" : "已完成");
            entry.put("statusClass", "parking".equals(r.getStatus()) ? "badge-warning" : "badge-success");
            entry.put("paymentStatus", r.getPaymentStatus());
            recordList.add(entry);
        }

        PageInfo<ParkingRecord> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("recordList", recordList);
        model.addAttribute("plateNumber", plateNumber);
        model.addAttribute("current", "record");
        return "parking-record-list";
    }

    @GetMapping("/enter")
    public String enterPage() {
        return "parking-record-enter";
    }

    @PostMapping("/enter")
    public String enter(@RequestParam String plateNumber,
                        @RequestParam(required = false) Long userId,
                        @RequestParam(required = false) Long spaceId,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if ("user".equals(session.getAttribute("userType")) && userId == null) {
            User u = (User) session.getAttribute("user");
            if (u != null) userId = u.getId();
        }
        try {
            parkingRecordService.enter(plateNumber, userId, spaceId);
            operationLogHelper.log(session, "车辆入场", "车辆入场：" + plateNumber);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                msg = e.getCause().getMessage();
            }
            redirectAttributes.addFlashAttribute("enterError", msg != null ? msg : "入场失败，请稍后重试");
            return "redirect:/parking-record/enter";
        }
        return "redirect:/parking-record/list";
    }

    @GetMapping("/exit")
    public String exitPage(Model model) {
        List<ParkingRecord> all = parkingRecordService.listRecords(null);
        List<Map<String, Object>> parkingList = new ArrayList<>();
        for (ParkingRecord r : all) {
            if ("parking".equals(r.getStatus())) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", r.getId());
                m.put("recordNo", r.getRecordNo());
                m.put("plateNumber", r.getPlateNumber());
                m.put("enterTimeStr", r.getEnterTime() != null ? r.getEnterTime().format(FMT) : "");
                long min = Duration.between(r.getEnterTime(), LocalDateTime.now()).toMinutes();
                m.put("durationMinutes", min);
                parkingList.add(m);
            }
        }
        model.addAttribute("parkingList", parkingList);
        return "parking-record-exit";
    }

    @GetMapping("/exit/{recordId}")
    public String exitLookup(@PathVariable Long recordId, Model model) {
        ParkingRecord record = parkingRecordService.getRecord(recordId);
        if (record == null || !"parking".equals(record.getStatus())) {
            model.addAttribute("lookupError", "记录不存在或已离场");
            return exitPage(model);
        }
        addFeePreviewToModel(record, model);
        return exitPage(model);
    }

    @PostMapping("/exit")
    public String exit(@RequestParam Long recordId,
                       @RequestParam String paymentMethod,
                       @RequestParam(required = false) Integer usePoints,
                       @RequestParam(required = false) Long userCouponId,
                       HttpSession session) {
        ParkingRecord record = parkingRecordService.getRecord(recordId);
        parkingRecordService.exit(recordId, paymentMethod,
                usePoints != null ? usePoints : 0,
                userCouponId);
        operationLogHelper.log(session, "车辆出场", "车辆出场，记录ID：" + recordId + (record != null ? "，车牌：" + record.getPlateNumber() : ""));
        return "redirect:/parking-record/list";
    }

    // ===== 用户端出场缴费 =====

    @GetMapping("/user-exit/{id}")
    public String userExitPage(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        ParkingRecord record = parkingRecordService.getRecord(id);
        if (record == null || !"parking".equals(record.getStatus())
                || !user.getId().equals(record.getUserId())) {
            return "redirect:/parking-record/list";
        }

        // 刷新用户信息
        User freshUser = userService.getUser(user.getId());
        session.setAttribute("user", freshUser);

        addFeePreviewToModel(record, model);

        // 用户积分
        int points = freshUser.getPoints() != null ? freshUser.getPoints() : 0;
        model.addAttribute("userPoints", points);
        int exchangeRate = getConfigInt("points_exchange_rate", 100);
        model.addAttribute("exchangeRate", exchangeRate);

        // 可用优惠券
        List<UserCoupon> userCoupons = userCouponService.listUnusedByUser(user.getId());
        List<Map<String, Object>> availableCoupons = new ArrayList<>();
        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponService.getCoupon(uc.getCouponId());
            if (coupon == null || coupon.getStatus() == null || coupon.getStatus() != 1) continue;
            if (coupon.getEndTime() != null && coupon.getEndTime().isBefore(LocalDateTime.now())) continue;

            Map<String, Object> entry = new HashMap<>();
            entry.put("userCouponId", uc.getId());
            entry.put("couponName", coupon.getCouponName());
            entry.put("couponType", coupon.getCouponType());
            entry.put("discountValue", coupon.getDiscountValue());
            if (coupon.getMinAmount() != null && coupon.getMinAmount().compareTo(BigDecimal.ZERO) > 0) {
                entry.put("minAmount", coupon.getMinAmount());
            }

            // 预生成优惠券标签
            StringBuilder label = new StringBuilder(coupon.getCouponName());
            if ("amount".equals(coupon.getCouponType())) {
                label.append(" - ¥").append(coupon.getDiscountValue());
            } else {
                label.append(" - ").append(coupon.getDiscountValue()).append("折");
            }
            if (coupon.getMinAmount() != null && coupon.getMinAmount().compareTo(BigDecimal.ZERO) > 0) {
                label.append(" (满").append(coupon.getMinAmount()).append("元可用)");
            }
            entry.put("couponLabel", label.toString());

            availableCoupons.add(entry);
        }
        model.addAttribute("availableCoupons", availableCoupons);

        return "parking-record-user-exit";
    }

    @PostMapping("/user-exit/{id}")
    public String userExit(@PathVariable Long id,
                           @RequestParam String paymentMethod,
                           @RequestParam(required = false) Integer usePoints,
                           @RequestParam(required = false) Long userCouponId,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        ParkingRecord record = parkingRecordService.getRecord(id);
        if (record == null || !"parking".equals(record.getStatus())
                || !user.getId().equals(record.getUserId())) {
            return "redirect:/parking-record/list";
        }
        parkingRecordService.exit(id, paymentMethod,
                usePoints != null ? usePoints : 0,
                userCouponId);
        return "redirect:/order/list";
    }

    // ===== 费用预览 =====

    private void addFeePreviewToModel(ParkingRecord record, Model model) {
        model.addAttribute("record", record);
        model.addAttribute("enterTimeStr", record.getEnterTime().format(FMT));
        model.addAttribute("durationMinutes",
                Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes());

        FeePreview fee = calculateFeePreview(record, null, null);
        model.addAttribute("originalFee", fee.originalFee);
        model.addAttribute("memberDiscount", fee.memberDiscount);
        model.addAttribute("couponDiscount", fee.couponDiscount);
        model.addAttribute("pointsDiscount", fee.pointsDiscount);
        model.addAttribute("discountFee", fee.totalDiscount);
        model.addAttribute("actualFee", fee.actualFee);
        model.addAttribute("memberLevelName", fee.memberLevelName);
        model.addAttribute("memberDiscountRate", fee.memberDiscountRate);
    }

    private FeePreview calculateFeePreview(ParkingRecord record, Integer usePoints, Long userCouponId) {
        FeePreview result = new FeePreview();
        long minutes = Duration.between(record.getEnterTime(), LocalDateTime.now()).toMinutes();

        List<ChargeRule> rules = chargeRuleService.listActive();
        ChargeRule rule = (rules != null && !rules.isEmpty()) ? rules.get(0) : null;

        Integer freeMinutes = getConfigInt("free_parking_minutes", rule != null ? rule.getFreeMinutes() : 15);
        BigDecimal unitPrice = getConfigDecimal("unit_price", rule != null ? rule.getUnitPrice() : new BigDecimal("5"));
        BigDecimal capPrice = getConfigDecimal("cap_amount", rule != null ? rule.getCapPrice() : null);

        int chargeableMinutes = Math.max(0, (int) minutes - freeMinutes);
        int hours = (int) Math.ceil(chargeableMinutes / 60.0);
        BigDecimal originalFee = unitPrice.multiply(BigDecimal.valueOf(hours));
        if (capPrice != null && originalFee.compareTo(capPrice) > 0) {
            originalFee = capPrice;
        }
        originalFee = originalFee.max(BigDecimal.ZERO);
        result.originalFee = originalFee.setScale(2, RoundingMode.HALF_UP);

        BigDecimal currentFee = result.originalFee;

        // 会员折扣
        Long userId = record.getUserId();
        MemberLevel level = null;
        if (userId != null) {
            User user = userService.getUser(userId);
            if (user != null && user.getMemberLevelId() != null) {
                level = memberLevelService.getLevel(user.getMemberLevelId());
                if (level != null && level.getDiscountRate() != null
                        && level.getDiscountRate().compareTo(BigDecimal.ONE) < 0) {
                    result.memberLevelName = level.getLevelName();
                    result.memberDiscountRate = level.getDiscountRate();
                    BigDecimal afterDiscount = originalFee.multiply(level.getDiscountRate())
                            .setScale(2, RoundingMode.HALF_UP);
                    result.memberDiscount = originalFee.subtract(afterDiscount);
                    currentFee = afterDiscount;
                }
            }
        }

        // 优惠券折扣预览
        if (userCouponId != null && currentFee.compareTo(BigDecimal.ZERO) > 0 && userId != null) {
            UserCoupon userCoupon = userCouponService.getUserCoupon(userCouponId);
            if (userCoupon != null && "unused".equals(userCoupon.getStatus())
                    && userCoupon.getUserId().equals(userId)) {
                Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());
                if (coupon != null && coupon.getStatus() != null && coupon.getStatus() == 1) {
                    BigDecimal minAmount = coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO;
                    if (currentFee.compareTo(minAmount) >= 0) {
                        if ("amount".equals(coupon.getCouponType())) {
                            BigDecimal discountValue = coupon.getDiscountValue() != null
                                    ? coupon.getDiscountValue() : BigDecimal.ZERO;
                            result.couponDiscount = discountValue.min(currentFee);
                            currentFee = currentFee.subtract(result.couponDiscount);
                        } else {
                            BigDecimal discountRate = coupon.getDiscountValue() != null
                                    ? coupon.getDiscountValue() : BigDecimal.ONE;
                            BigDecimal afterCoupon = currentFee.multiply(discountRate)
                                    .setScale(2, RoundingMode.HALF_UP);
                            result.couponDiscount = currentFee.subtract(afterCoupon);
                            currentFee = afterCoupon;
                        }
                    }
                }
            }
        }

        // 积分抵扣预览
        if (usePoints != null && usePoints > 0 && userId != null && currentFee.compareTo(BigDecimal.ZERO) > 0) {
            User user = userService.getUser(userId);
            int userPoints = user != null && user.getPoints() != null ? user.getPoints() : 0;
            int actualPoints = Math.min(usePoints, userPoints);
            if (actualPoints > 0) {
                int exchangeRate = getConfigInt("points_exchange_rate", 100);
                BigDecimal pointsToYuan = new BigDecimal(actualPoints)
                        .divide(new BigDecimal(exchangeRate), 2, RoundingMode.HALF_UP);
                result.pointsDiscount = pointsToYuan.min(currentFee);
                currentFee = currentFee.subtract(result.pointsDiscount);
            }
        }

        result.actualFee = currentFee.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        result.totalDiscount = result.originalFee.subtract(result.actualFee).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        return result;
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

    private static class FeePreview {
        BigDecimal originalFee = BigDecimal.ZERO;
        BigDecimal memberDiscount = BigDecimal.ZERO;
        BigDecimal couponDiscount = BigDecimal.ZERO;
        BigDecimal pointsDiscount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal actualFee = BigDecimal.ZERO;
        String memberLevelName;
        BigDecimal memberDiscountRate;
    }
}
