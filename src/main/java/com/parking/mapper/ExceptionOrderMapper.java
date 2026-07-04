package com.parking.mapper;

import com.parking.entity.ExceptionOrder;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ExceptionOrderMapper {
    @Select("SELECT * FROM exception_order WHERE id = #{id}")
    ExceptionOrder selectById(Long id);

    @Insert("INSERT INTO exception_order (order_id, exception_type, exception_desc, handle_status, handle_result, create_time) VALUES (#{orderId}, #{exceptionType}, #{exceptionDesc}, #{handleStatus}, #{handleResult}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExceptionOrder order);

    @Update("UPDATE exception_order SET handle_status=#{handleStatus}, handle_result=#{handleResult} WHERE id=#{id}")
    int update(ExceptionOrder order);

    @Select("SELECT * FROM exception_order WHERE handle_status = 'pending'")
    List<ExceptionOrder> selectPending();
}