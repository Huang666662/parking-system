package com.parking.mapper;

import com.parking.entity.CargoWhitelist;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CargoWhitelistMapper {
    @Select("SELECT * FROM cargo_whitelist WHERE id = #{id}")
    CargoWhitelist selectById(Long id);

    @Insert("INSERT INTO cargo_whitelist (plate_number, merchant_id, driver_name, driver_phone, allow_start_time, allow_end_time, effective_start_date, effective_end_date, status) VALUES (#{plateNumber}, #{merchantId}, #{driverName}, #{driverPhone}, #{allowStartTime}, #{allowEndTime}, #{effectiveStartDate}, #{effectiveEndDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CargoWhitelist whitelist);

    @Update("UPDATE cargo_whitelist SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT * FROM cargo_whitelist")
    List<CargoWhitelist> selectAll();

    @Select("SELECT * FROM cargo_whitelist WHERE merchant_id = #{merchantId}")
    List<CargoWhitelist> selectByMerchantId(@Param("merchantId") Long merchantId);
}