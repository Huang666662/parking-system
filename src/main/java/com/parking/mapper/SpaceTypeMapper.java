package com.parking.mapper;

import com.parking.entity.SpaceType;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SpaceTypeMapper {
    @Select("SELECT * FROM space_type WHERE id = #{id}")
    SpaceType selectById(Integer id);

    @Insert("INSERT INTO space_type (type_name, extra_fee_rate, icon) VALUES (#{typeName}, #{extraFeeRate}, #{icon})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SpaceType type);

    @Update("UPDATE space_type SET type_name=#{typeName}, extra_fee_rate=#{extraFeeRate}, icon=#{icon} WHERE id=#{id}")
    int update(SpaceType type);

    @Delete("DELETE FROM space_type WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM space_type")
    List<SpaceType> selectAll();
}