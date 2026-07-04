package com.parking.mapper;

import com.parking.entity.CargoApplication;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CargoApplicationMapper {
    @Select("SELECT * FROM cargo_application WHERE id = #{id}")
    CargoApplication selectById(Long id);

    @Insert("INSERT INTO cargo_application (apply_no, plate_number, merchant_name, driver_name, driver_phone, delivery_image, permit_duration, status, apply_time) VALUES (#{applyNo}, #{plateNumber}, #{merchantName}, #{driverName}, #{driverPhone}, #{deliveryImage}, #{permitDuration}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CargoApplication application);

    @Update("UPDATE cargo_application SET status=#{status}, approve_time=NOW() WHERE id=#{id}")
    int approve(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT * FROM cargo_application")
    List<CargoApplication> selectAll();

    @Select("SELECT * FROM cargo_application WHERE status = 'pending'")
    List<CargoApplication> selectPending();
}