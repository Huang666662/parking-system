package com.parking.mapper;

import com.parking.entity.WriteOffRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface WriteOffRecordMapper {
    @Select("SELECT * FROM write_off_record WHERE id = #{id}")
    WriteOffRecord selectById(Long id);

    @Insert("INSERT INTO write_off_record (merchant_id, ticket_no, plate_number, free_minutes, write_off_time) VALUES (#{merchantId}, #{ticketNo}, #{plateNumber}, #{freeMinutes}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WriteOffRecord record);

    @Select("SELECT * FROM write_off_record WHERE merchant_id = #{merchantId} ORDER BY write_off_time DESC")
    List<WriteOffRecord> selectByMerchantId(@Param("merchantId") Long merchantId);
}