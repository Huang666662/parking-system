package com.parking.mapper;

import com.parking.entity.FinancialRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FinancialRecordMapper {
    @Select("SELECT * FROM financial_record WHERE id = #{id}")
    FinancialRecord selectById(Long id);

    @Insert("INSERT INTO financial_record (order_id, amount, payment_method, record_type, record_time) VALUES (#{orderId}, #{amount}, #{paymentMethod}, #{recordType}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FinancialRecord record);

    @Select("SELECT * FROM financial_record ORDER BY record_time DESC")
    List<FinancialRecord> selectAll();
}