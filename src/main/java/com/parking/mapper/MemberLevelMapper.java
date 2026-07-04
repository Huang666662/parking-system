package com.parking.mapper;

import com.parking.entity.MemberLevel;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MemberLevelMapper {
    @Insert("INSERT INTO member_level (level_name, level_code, min_points, discount_rate, monthly_free_minutes) VALUES (#{levelName}, #{levelCode}, #{minPoints}, #{discountRate}, #{monthlyFreeMinutes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MemberLevel level);

    @Delete("DELETE FROM member_level WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM member_level")
    List<MemberLevel> selectAll();

    @Select("SELECT * FROM member_level WHERE id = #{id}")
    MemberLevel selectById(Integer id);
}