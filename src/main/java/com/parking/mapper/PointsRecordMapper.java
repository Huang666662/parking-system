package com.parking.mapper;

import com.parking.entity.PointsRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PointsRecordMapper {
    @Select("SELECT * FROM points_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PointsRecord> selectByUserId(@Param("userId") Long userId);
}
