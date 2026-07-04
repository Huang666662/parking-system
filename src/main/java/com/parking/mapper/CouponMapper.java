package com.parking.mapper;

import com.parking.entity.Coupon;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CouponMapper {
    @Select("SELECT * FROM coupon WHERE id = #{id}")
    Coupon selectById(Long id);

    @Insert("INSERT INTO coupon (coupon_name, coupon_type, discount_value, min_amount, total_quantity, remaining_quantity, end_time, status) VALUES (#{couponName}, #{couponType}, #{discountValue}, #{minAmount}, #{totalQuantity}, #{remainingQuantity}, #{endTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Coupon coupon);

    @Update("UPDATE coupon SET remaining_quantity = remaining_quantity - 1 WHERE id = #{id} AND remaining_quantity > 0")
    int decrementRemaining(@Param("id") Long id);

    @Select("SELECT * FROM coupon WHERE status = 1 AND end_time > NOW()")
    List<Coupon> selectAvailable();
}