package com.parking.mapper;

import com.parking.entity.PaymentRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PaymentRecordMapper {
    @Select("SELECT * FROM payment_record WHERE id = #{id}")
    PaymentRecord selectById(Long id);

    @Insert("INSERT INTO payment_record (payment_no, record_id, amount, payment_method, discount_points, coupon_id, status, payment_time) VALUES (#{paymentNo}, #{recordId}, #{amount}, #{paymentMethod}, #{discountPoints}, #{couponId}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PaymentRecord record);

    @Select("SELECT * FROM payment_record WHERE record_id = #{recordId}")
    PaymentRecord selectByRecordId(@Param("recordId") Long recordId);

    @Select("SELECT * FROM payment_record ORDER BY payment_time DESC")
    List<PaymentRecord> selectAll();
}