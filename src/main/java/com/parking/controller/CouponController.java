package com.parking.controller;

import com.parking.entity.Coupon;
import com.parking.service.ICouponService;
import com.parking.util.OperationLogHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private ICouponService couponService;

    @Autowired
    private OperationLogHelper operationLogHelper;

    private boolean hasAccess(HttpSession session) {
        String role = (String) session.getAttribute("adminRole");
        return "super_admin".equals(role) || "system_admin".equals(role);
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        List<Coupon> list = couponService.listAvailable();
        model.addAttribute("coupons", list);
        model.addAttribute("current", "coupon");
        return "coupon-list";
    }

    @PostMapping("/add")
    public String add(@RequestParam String couponName,
                      @RequestParam String couponType,
                      @RequestParam java.math.BigDecimal discountValue,
                      @RequestParam java.math.BigDecimal minAmount,
                      @RequestParam Integer totalQuantity,
                      @RequestParam String endTime,
                      HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        Coupon coupon = new Coupon();
        coupon.setCouponName(couponName);
        coupon.setCouponType(couponType);
        coupon.setDiscountValue(discountValue);
        coupon.setMinAmount(minAmount);
        coupon.setTotalQuantity(totalQuantity);
        coupon.setRemainingQuantity(totalQuantity);
        coupon.setEndTime(LocalDateTime.parse(endTime.replace(" ", "T")));
        coupon.setStatus(1);
        couponService.addCoupon(coupon);
        operationLogHelper.log(session, "新增优惠券", "新增优惠券：" + couponName);
        return "redirect:/coupon/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!hasAccess(session)) {
            return "redirect:/index";
        }
        couponService.decrementRemaining(id);
        operationLogHelper.log(session, "下架优惠券", "下架优惠券ID：" + id);
        return "redirect:/coupon/list";
    }
}
