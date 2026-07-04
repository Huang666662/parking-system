package com.parking.service;

import com.parking.entity.UserCoupon;
import java.util.List;

public interface IUserCouponService {
    int addUserCoupon(UserCoupon userCoupon);
    int markUsed(Long id);
    UserCoupon getUserCoupon(Long id);
    List<UserCoupon> listUnusedByUser(Long userId);
}