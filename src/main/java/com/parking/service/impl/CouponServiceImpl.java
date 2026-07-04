package com.parking.service.impl;

import com.parking.entity.Coupon;
import com.parking.mapper.CouponMapper;
import com.parking.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CouponServiceImpl implements ICouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public int addCoupon(Coupon coupon) {
        return couponMapper.insert(coupon);
    }

    @Override
    public int decrementRemaining(Long id) {
        return couponMapper.decrementRemaining(id);
    }

    @Override
    public Coupon getCoupon(Long id) {
        return couponMapper.selectById(id);
    }

    @Override
    public List<Coupon> listAvailable() {
        return couponMapper.selectAvailable();
    }
}