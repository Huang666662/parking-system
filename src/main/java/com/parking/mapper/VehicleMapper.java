package com.parking.mapper;

import com.parking.entity.Vehicle;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VehicleMapper {
    @Select("SELECT * FROM vehicle WHERE id = #{id}")
    Vehicle selectById(Long id);

    @Insert("INSERT INTO vehicle (user_id, plate_number, vehicle_type_id, is_default, status, bind_time) VALUES (#{userId}, #{plateNumber}, #{vehicleTypeId}, #{isDefault}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Vehicle vehicle);

    @Update("UPDATE vehicle SET user_id=#{userId}, plate_number=#{plateNumber}, vehicle_type_id=#{vehicleTypeId}, is_default=#{isDefault}, status=#{status} WHERE id=#{id}")
    int update(Vehicle vehicle);

    @Delete("DELETE FROM vehicle WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM vehicle WHERE user_id = #{userId}")
    List<Vehicle> selectByUserId(@Param("userId") Long userId);
}