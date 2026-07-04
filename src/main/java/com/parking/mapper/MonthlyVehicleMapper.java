package com.parking.mapper;

import com.parking.entity.MonthlyVehicle;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MonthlyVehicleMapper {
    @Select("SELECT * FROM monthly_vehicle WHERE id = #{id}")
    MonthlyVehicle selectById(Long id);

    @Insert("INSERT INTO monthly_vehicle (user_id, plate_number, package_id, start_date, end_date, status, create_time) VALUES (#{userId}, #{plateNumber}, #{packageId}, #{startDate}, #{endDate}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MonthlyVehicle vehicle);

    @Update("UPDATE monthly_vehicle SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT * FROM monthly_vehicle WHERE plate_number = #{plateNumber}")
    MonthlyVehicle selectByPlate(@Param("plateNumber") String plateNumber);

    @Select("SELECT * FROM monthly_vehicle WHERE end_date < NOW() AND status = 'active'")
    List<MonthlyVehicle> selectExpired();

    @Select("SELECT * FROM monthly_vehicle ORDER BY create_time DESC")
    List<MonthlyVehicle> selectAll();
}