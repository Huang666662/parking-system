package com.parking.service;

import com.parking.entity.Coupon;
import java.util.List;

public interface ICouponService {
    int addCoupon(Coupon coupon);
    int decrementRemaining(Long id);
    Coupon getCoupon(Long id);
    List<Coupon> listAvailable();
}