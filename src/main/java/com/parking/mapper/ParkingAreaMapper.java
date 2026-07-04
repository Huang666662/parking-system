package com.parking.mapper;

import com.parking.entity.ParkingArea;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ParkingAreaMapper {
    @Select("SELECT * FROM parking_area WHERE id = #{id}")
    ParkingArea selectById(Integer id);

    @Insert("INSERT INTO parking_area (area_name, floor, total_spaces, description) VALUES (#{areaName}, #{floor}, #{totalSpaces}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ParkingArea area);

    @Update("UPDATE parking_area SET area_name=#{areaName}, floor=#{floor}, total_spaces=#{totalSpaces}, description=#{description} WHERE id=#{id}")
    int update(ParkingArea area);

    @Delete("DELETE FROM parking_area WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM parking_area")
    List<ParkingArea> selectAll();
}