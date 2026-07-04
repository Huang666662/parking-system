package com.parking.mapper;

import com.parking.entity.UserCoupon;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserCouponMapper {
    @Select("SELECT * FROM user_coupon WHERE id = #{id}")
    UserCoupon selectById(Long id);

    @Insert("INSERT INTO user_coupon (user_id, coupon_id, status, receive_time) VALUES (#{userId}, #{couponId}, 'unused', NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCoupon userCoupon);

    @Update("UPDATE user_coupon SET status='used', use_time=NOW() WHERE id = #{id}")
    int markUsed(@Param("id") Long id);

    @Select("SELECT * FROM user_coupon WHERE user_id = #{userId} AND status = 'unused'")
    List<UserCoupon> selectUnusedByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM user_coupon WHERE user_id = #{userId} ORDER BY receive_time DESC")
    List<UserCoupon> selectAllByUser(@Param("userId") Long userId);
}