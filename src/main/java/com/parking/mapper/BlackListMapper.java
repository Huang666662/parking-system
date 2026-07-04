package com.parking.mapper;

import com.parking.entity.BlackList;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BlackListMapper {
    @Select("SELECT * FROM black_list WHERE id = #{id}")
    BlackList selectById(Long id);

    @Insert("INSERT INTO black_list (plate_number, reason, operator_id, create_time) VALUES (#{plateNumber}, #{reason}, #{operatorId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BlackList blackList);

    @Select("SELECT * FROM black_list WHERE plate_number = #{plateNumber}")
    BlackList selectByPlate(@Param("plateNumber") String plateNumber);

    @Delete("DELETE FROM black_list WHERE plate_number = #{plateNumber}")
    int deleteByPlate(@Param("plateNumber") String plateNumber);

    @Select("SELECT * FROM black_list ORDER BY create_time DESC")
    List<BlackList> selectAll();
}