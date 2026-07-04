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

import java.util.*;

@Controller
@RequestMapping("/member")
public class MemberController {

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
        }
        List<MemberLevel> allLevels = memberLevelService.listAll();
        model.addAttribute("allLevels", allLevels);

        // 2. 积分信息
        model.addAttribute("points", user.getPoints() != null ? user.getPoints() : 0);
        List<PointsRecord> pointsRecords = Collections.emptyList();
        try {
            pointsRecords = pointsRecordMapper.selectByUserId(user.getId());
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
            entry.put("userCouponId", uc.getId());
            entry.put("couponName", coupon.getCouponName());
            entry.put("couponType", coupon.getCouponType());
            entry.put("discountValue", coupon.getDiscountValue());
            entry.put("minAmount", coupon.getMinAmount());
            entry.put("endTime", coupon.getEndTime());
            entry.put("receiveTime", uc.getReceiveTime());
            entry.put("useTime", uc.getUseTime());
            entry.put("status", uc.getStatus());

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
}
