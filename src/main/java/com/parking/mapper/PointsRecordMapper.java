package com.parking.mapper;

import com.parking.entity.PointsRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PointsRecordMapper {
    @Select("SELECT * FROM points_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PointsRecord> selectByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO points_record (user_id, points, type, description, create_time) VALUES (#{userId}, #{points}, #{type}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointsRecord record);
}
