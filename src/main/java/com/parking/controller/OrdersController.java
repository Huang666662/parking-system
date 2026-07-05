package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.*;
import com.parking.mapper.PointsRecordMapper;
import com.parking.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrdersController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private IOrdersService ordersService;

    @Autowired
    private IPaymentRecordService paymentRecordService;

    @Autowired
    private IFinancialRecordService financialRecordService;

    @Autowired
    private IParkingRecordService parkingRecordService;

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICouponService couponService;

    @Autowired
    private IUserCouponService userCouponService;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private ISystemConfigService systemConfigService;

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
        List<Orders> list = ordersService.listOrders(plateNumber);
        if ("user".equals(userType) && userId != null) {
            final Long currentUserId = userId;
            list.removeIf(o -> !currentUserId.equals(o.getUserId()));
        }
        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Orders o : list) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", o.getId());
            entry.put("orderNo", o.getOrderNo());
            entry.put("plateNumber", o.getPlateNumber());
            entry.put("totalAmount", o.getTotalAmount());
            entry.put("paidAmount", o.getPaidAmount());
            entry.put("status", o.getStatus());
            entry.put("statusLabel", getStatusLabel(o.getStatus()));
            entry.put("statusClass", getStatusClass(o.getStatus()));
            entry.put("createTimeStr", o.getCreateTime() != null ? o.getCreateTime().format(FMT) : "");
            orderList.add(entry);
        }
        PageInfo<Orders> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        model.addAttribute("orderList", orderList);
        model.addAttribute("plateNumber", plateNumber);
        model.addAttribute("current", "order");
        return "order-list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Orders order = ordersService.getOrder(id);
        model.addAttribute("order", order);
        model.addAttribute("createTimeStr", order.getCreateTime() != null ? order.getCreateTime().format(FMT) : "");
        return "order-detail";
    }

    @GetMapping("/pay/{id}")
    public String payPage(@PathVariable Long id, Model model, HttpSession session) {
        Orders order = ordersService.getOrder(id);
        if (order == null) {
            return "redirect:/order/list";
        }
        model.addAttribute("order", order);
        model.addAttribute("createTimeStr", order.getCreateTime() != null ? order.getCreateTime().format(FMT) : "");

        User user = (User) session.getAttribute("user");
        if (user != null) {
            User freshUser = userService.getUser(user.getId());
            int points = freshUser.getPoints() != null ? freshUser.getPoints() : 0;
            model.addAttribute("userPoints", points);
            int exchangeRate = getConfigInt("points_exchange_rate", 100);
            model.addAttribute("exchangeRate", exchangeRate);

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
        }

        return "order-pay";
    }

    @PostMapping("/pay/{id}")
    public String doPay(@PathVariable Long id,
                        @RequestParam String paymentMethod,
                        @RequestParam(required = false) Integer usePoints,
                        @RequestParam(required = false) Long userCouponId,
                        HttpSession session) {
        Orders order = ordersService.getOrder(id);
        if (order == null || !"unpaid".equals(order.getStatus())) {
            return "redirect:/order/list";
        }

        User sessionUser = (User) session.getAttribute("user");
        User dbUser = sessionUser != null ? userService.getUser(sessionUser.getId()) : null;

        BigDecimal totalAmount = order.getTotalAmount();
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        BigDecimal pointsDiscountAmount = BigDecimal.ZERO;
        int actualUsePoints = 0;
        Long actualCouponId = null;

        // 应用优惠券
        if (userCouponId != null && dbUser != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            UserCoupon userCoupon = userCouponService.getUserCoupon(userCouponId);
            if (userCoupon != null && "unused".equals(userCoupon.getStatus())
                    && userCoupon.getUserId().equals(dbUser.getId())) {
                Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());
                if (coupon != null && coupon.getStatus() != null && coupon.getStatus() == 1
                        && (coupon.getEndTime() == null || coupon.getEndTime().isAfter(LocalDateTime.now()))) {
                    BigDecimal minAmount = coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO;
                    if (totalAmount.compareTo(minAmount) >= 0) {
                        if ("amount".equals(coupon.getCouponType())) {
                            BigDecimal discountValue = coupon.getDiscountValue() != null
                                    ? coupon.getDiscountValue() : BigDecimal.ZERO;
                            couponDiscountAmount = discountValue.min(totalAmount);
                            totalAmount = totalAmount.subtract(couponDiscountAmount);
                        } else {
                            BigDecimal discountRate = coupon.getDiscountValue() != null
                                    ? coupon.getDiscountValue() : BigDecimal.ONE;
                            BigDecimal afterCoupon = totalAmount.multiply(discountRate)
                                    .setScale(2, RoundingMode.HALF_UP);
                            couponDiscountAmount = totalAmount.subtract(afterCoupon);
                            totalAmount = afterCoupon;
                        }
                        actualCouponId = coupon.getId();
                        userCouponService.markUsed(userCouponId);
                    }
                }
            }
        }

        // 应用积分抵扣
        if (usePoints != null && usePoints > 0 && dbUser != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            int userPoints = dbUser.getPoints() != null ? dbUser.getPoints() : 0;
            actualUsePoints = Math.min(usePoints, userPoints);
            if (actualUsePoints > 0) {
                int exchangeRate = getConfigInt("points_exchange_rate", 100);
                BigDecimal pointsToYuan = new BigDecimal(actualUsePoints)
                        .divide(new BigDecimal(exchangeRate), 2, RoundingMode.HALF_UP);
                pointsDiscountAmount = pointsToYuan.min(totalAmount);
                totalAmount = totalAmount.subtract(pointsDiscountAmount);

                dbUser.setPoints(userPoints - actualUsePoints);
                userService.updateUser(dbUser);

                PointsRecord pointsRecord = new PointsRecord();
                pointsRecord.setUserId(dbUser.getId());
                pointsRecord.setPoints(-actualUsePoints);
                pointsRecord.setType("支出");
                pointsRecord.setDescription("订单支付抵扣 -" + actualUsePoints + "积分");
                pointsRecordMapper.insert(pointsRecord);
            }
        }

        BigDecimal finalAmount = totalAmount.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        // 更新订单
        order.setPaidAmount(finalAmount);
        order.setStatus("paid");
        ordersService.updateOrder(order);

        // 创建支付记录
        PaymentRecord payment = new PaymentRecord();
        payment.setPaymentNo("PAY" + System.currentTimeMillis());
        payment.setRecordId(null);
        payment.setAmount(finalAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setDiscountPoints(actualUsePoints);
        payment.setCouponId(actualCouponId);
        payment.setStatus("success");
        paymentRecordService.addPayment(payment);

        // 创建财务记录
        FinancialRecord financial = new FinancialRecord();
        financial.setOrderId(order.getId());
        financial.setAmount(finalAmount);
        financial.setPaymentMethod(paymentMethod);
        financial.setRecordType("income");
        financialRecordService.addFinancialRecord(financial);

        // 积分奖励
        if (dbUser != null) {
            int earnedPoints = finalAmount.intValue();
            if (earnedPoints > 0) {
                dbUser = userService.getUser(dbUser.getId());
                dbUser.setPoints((dbUser.getPoints() != null ? dbUser.getPoints() : 0) + earnedPoints);
                userService.updateUser(dbUser);
                session.setAttribute("user", dbUser);

                PointsRecord earnRecord = new PointsRecord();
                earnRecord.setUserId(dbUser.getId());
                earnRecord.setPoints(earnedPoints);
                earnRecord.setType("收入");
                earnRecord.setDescription("支付消费奖励 +" + earnedPoints + "积分");
                pointsRecordMapper.insert(earnRecord);
            }
        }

        // 更新关联的停车记录
        try {
            List<ParkingRecord> records = parkingRecordService.listRecords(order.getPlateNumber());
            if (records != null) {
                for (ParkingRecord pr : records) {
                    if ("parking".equals(pr.getStatus())) {
                        pr.setExitTime(LocalDateTime.now());
                        pr.setDurationMinutes((int) java.time.Duration.between(pr.getEnterTime(), LocalDateTime.now()).toMinutes());
                        pr.setStatus("completed");
                        pr.setPaymentStatus("paid");
                        parkingRecordService.updateRecord(pr);
                        if (pr.getSpaceId() != null) {
                            try {
                                ParkingSpace space = parkingSpaceService.getSpace(pr.getSpaceId());
                                if (space != null) {
                                    space.setStatus("free");
                                    parkingSpaceService.updateSpace(space);
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return "redirect:/order/list";
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

    private String getStatusLabel(String status) {
        if ("paid".equals(status)) return "已支付";
        if ("unpaid".equals(status)) return "待支付";
        if ("completed".equals(status)) return "已完成";
        return status;
    }

    private String getStatusClass(String status) {
        if ("paid".equals(status) || "completed".equals(status)) return "badge-success";
        if ("unpaid".equals(status)) return "badge-warning";
        return "badge-danger";
    }
}
