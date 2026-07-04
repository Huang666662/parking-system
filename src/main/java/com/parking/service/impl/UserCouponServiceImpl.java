package com.parking.service.impl;

import com.parking.entity.UserCoupon;
import com.parking.mapper.UserCouponMapper;
import com.parking.service.IUserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserCouponServiceImpl implements IUserCouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public int addUserCoupon(UserCoupon userCoupon) {
        return userCouponMapper.insert(userCoupon);
    }

    @Override
    public int markUsed(Long id) {
        return userCouponMapper.markUsed(id);
    }

    @Override
    public UserCoupon getUserCoupon(Long id) {
        return userCouponMapper.selectById(id);
    }

    @Override
    public List<UserCoupon> listUnusedByUser(Long userId) {
        return userCouponMapper.selectUnusedByUser(userId);
    }
}