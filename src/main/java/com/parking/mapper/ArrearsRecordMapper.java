package com.parking.mapper;

import com.parking.entity.ArrearsRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ArrearsRecordMapper {
    @Select("SELECT * FROM arrears_record WHERE id = #{id}")
    ArrearsRecord selectById(Long id);

    @Insert("INSERT INTO arrears_record (order_id, plate_number, arrears_amount, status, remind_count, create_time) VALUES (#{orderId}, #{plateNumber}, #{arrearsAmount}, #{status}, #{remindCount}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ArrearsRecord record);

    @Update("UPDATE arrears_record SET remind_count = remind_count + 1 WHERE id = #{id}")
    int incrementRemind(@Param("id") Long id);

    @Select("SELECT * FROM arrears_record WHERE status = 'unpaid'")
    List<ArrearsRecord> selectUnpaid();
}