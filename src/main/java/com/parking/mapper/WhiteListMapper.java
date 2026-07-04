package com.parking.mapper;

import com.parking.entity.WhiteList;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface WhiteListMapper {
    @Select("SELECT * FROM white_list WHERE id = #{id}")
    WhiteList selectById(Long id);

    @Insert("INSERT INTO white_list (plate_number, user_id, expire_date, status) VALUES (#{plateNumber}, #{userId}, #{expireDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WhiteList whiteList);

    @Update("UPDATE white_list SET status=#{status} WHERE plate_number = #{plateNumber}")
    int updateStatusByPlate(@Param("plateNumber") String plateNumber, @Param("status") String status);

    @Select("SELECT * FROM white_list WHERE plate_number = #{plateNumber} AND status = 'active' AND expire_date > NOW()")
    WhiteList selectActiveByPlate(@Param("plateNumber") String plateNumber);

    @Select("SELECT * FROM white_list ORDER BY id DESC")
    List<WhiteList> selectAll();
}
