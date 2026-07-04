package com.parking.mapper;

import com.parking.entity.ParkingSpace;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ParkingSpaceMapper {
    @Select("SELECT * FROM parking_space WHERE id = #{id}")
    ParkingSpace selectById(Long id);

    @Insert("INSERT INTO parking_space (space_number, space_name, area_id, space_type_id, status, is_enabled, location_desc, remark, create_time) VALUES (#{spaceNumber}, #{spaceName}, #{areaId}, #{spaceTypeId}, #{status}, #{isEnabled}, #{locationDesc}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ParkingSpace space);

    @Update("UPDATE parking_space SET space_number=#{spaceNumber}, space_name=#{spaceName}, area_id=#{areaId}, space_type_id=#{spaceTypeId}, status=#{status}, is_enabled=#{isEnabled}, location_desc=#{locationDesc}, remark=#{remark} WHERE id=#{id}")
    int update(ParkingSpace space);

    @Delete("DELETE FROM parking_space WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM parking_space")
    List<ParkingSpace> selectAll();

    @Select("SELECT * FROM parking_space WHERE status = 'free' AND is_enabled = 1")
    List<ParkingSpace> selectFreeSpaces();
}