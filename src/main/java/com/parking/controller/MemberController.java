package com.parking.controller;

import com.parking.entity.*;
import com.parking.mapper.*;
import com.parking.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/member")
public class MemberController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private IUserService userService;

    @Autowired
    private IMemberLevelService memberLevelService;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @GetMapping
    public String memberCenter(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        User user = userService.getUser(sessionUser.getId());

        // 1. 会员等级信息
        if (user.getMemberLevelId() != null) {
            MemberLevel level = memberLevelService.getLevel(user.getMemberLevelId());
            model.addAttribute("memberLevel", level);
            model.addAttribute("levelBadgeColor", getBadgeColor(level.getLevelCode()));
            model.addAttribute("levelIcon", getLevelIcon(level.getLevelCode()));
            if (level.getDiscountRate() != null) {
                model.addAttribute("discountRateStr", level.getDiscountRate().toString());
            }
        }
        List<MemberLevel> allLevels = memberLevelService.listAll();
        model.addAttribute("allLevels", allLevels);
        if (allLevels.size() > 1 && user.getMemberLevelId() != null) {
            int idx = 0;
            for (int i = 0; i < allLevels.size(); i++) {
                if (allLevels.get(i).getId().equals(user.getMemberLevelId())) {
                    idx = i + 1;
                    break;
                }
            }
            int pct = idx >= allLevels.size() ? 100 : (idx * 100 / allLevels.size());
            model.addAttribute("levelProgressPct", pct);
        }

        // 2. 积分信息
        int points = user.getPoints() != null ? user.getPoints() : 0;
        model.addAttribute("points", points);
        List<Map<String, Object>> pointsRecords = new ArrayList<>();
        try {
            for (PointsRecord r : pointsRecordMapper.selectByUserId(user.getId())) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("time", r.getCreateTime() != null ? r.getCreateTime().format(FMT) : "");
                entry.put("type", r.getPoints() != null && r.getPoints() > 0 ? "收入" : "支出");
                entry.put("typeClass", r.getPoints() != null && r.getPoints() > 0 ? "badge-success" : "badge-danger");
                int val = r.getPoints() != null ? r.getPoints() : 0;
                entry.put("pointsStr", val > 0 ? "+" + val : String.valueOf(val));
                entry.put("description", r.getDescription() != null ? r.getDescription() : "");
                pointsRecords.add(entry);
            }
        } catch (Exception ignored) {
        }
        model.addAttribute("pointsRecords", pointsRecords);

        // 3. 优惠券信息
        List<UserCoupon> userCoupons = userCouponMapper.selectAllByUser(user.getId());
        List<Map<String, Object>> availableCoupons = new ArrayList<>();
        List<Map<String, Object>> usedCoupons = new ArrayList<>();
        List<Map<String, Object>> expiredCoupons = new ArrayList<>();

        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            if (coupon == null) continue;

            Map<String, Object> entry = new HashMap<>();
            entry.put("couponName", coupon.getCouponName());
            entry.put("isAmount", "amount".equals(coupon.getCouponType()));
            entry.put("displayValue", coupon.getDiscountValue() != null ? coupon.getDiscountValue().toString() : "");
            if (coupon.getMinAmount() != null && coupon.getMinAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                entry.put("minAmountStr", coupon.getMinAmount().toString());
            }
            if (coupon.getEndTime() != null) {
                entry.put("endTimeStr", coupon.getEndTime().format(FMT));
            }
            if (uc.getReceiveTime() != null) {
                entry.put("receiveTimeStr", uc.getReceiveTime().format(FMT));
            }
            if (uc.getUseTime() != null) {
                entry.put("useTimeStr", uc.getUseTime().format(FMT));
            }

            if ("used".equals(uc.getStatus())) {
                usedCoupons.add(entry);
            } else if (coupon.getEndTime() != null && coupon.getEndTime().isBefore(java.time.LocalDateTime.now())) {
                expiredCoupons.add(entry);
            } else {
                availableCoupons.add(entry);
            }
        }

        model.addAttribute("availableCoupons", availableCoupons);
        model.addAttribute("usedCoupons", usedCoupons);
        model.addAttribute("expiredCoupons", expiredCoupons);

        return "member-center";
    }

    private String getBadgeColor(String levelCode) {
        if ("diamond".equals(levelCode)) return "#f9f0ff";
        if ("gold".equals(levelCode)) return "#fffbe6";
        if ("silver".equals(levelCode)) return "#f0f5ff";
        return "#f5f5f5";
    }

    private String getLevelIcon(String levelCode) {
        if ("diamond".equals(levelCode)) return "💎";
        if ("gold".equals(levelCode)) return "🥇";
        if ("silver".equals(levelCode)) return "🥈";
        return "🥉";
    }
}
