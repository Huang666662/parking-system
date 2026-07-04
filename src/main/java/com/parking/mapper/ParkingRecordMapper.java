package com.parking.mapper;

import com.parking.entity.ParkingRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ParkingRecordMapper {
    @Select("SELECT * FROM parking_record WHERE id = #{id}")
    ParkingRecord selectById(Long id);

    @Insert("INSERT INTO parking_record (record_no, plate_number, user_id, space_id, enter_time, status) VALUES (#{recordNo}, #{plateNumber}, #{userId}, #{spaceId}, NOW(), 'parking')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ParkingRecord record);

    @Update("UPDATE parking_record SET exit_time=#{exitTime}, duration_minutes=#{durationMinutes}, original_fee=#{originalFee}, discount_fee=#{discountFee}, actual_fee=#{actualFee}, status=#{status}, payment_status=#{paymentStatus} WHERE id=#{id}")
    int update(ParkingRecord record);

    @Select("SELECT * FROM parking_record WHERE plate_number = #{plateNumber} AND status = 'parking'")
    ParkingRecord selectCurrentByPlate(@Param("plateNumber") String plateNumber);

    @Select("SELECT * FROM parking_record WHERE plate_number LIKE CONCAT('%', #{plateNumber}, '%') ORDER BY enter_time DESC")
    List<ParkingRecord> selectByPlate(@Param("plateNumber") String plateNumber);

    @Select("SELECT * FROM parking_record ORDER BY enter_time DESC")
    List<ParkingRecord> selectAll();
}