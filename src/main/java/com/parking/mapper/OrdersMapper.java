package com.parking.mapper;

import com.parking.entity.Orders;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrdersMapper {
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders selectById(Long id);

    @Insert("INSERT INTO orders (order_no, plate_number, user_id, total_amount, paid_amount, status, create_time) VALUES (#{orderNo}, #{plateNumber}, #{userId}, #{totalAmount}, #{paidAmount}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Orders order);

    @Update("UPDATE orders SET paid_amount=#{paidAmount}, status=#{status} WHERE id=#{id}")
    int update(Orders order);

    @Select("SELECT * FROM orders WHERE plate_number LIKE CONCAT('%', #{plateNumber}, '%') ORDER BY create_time DESC")
    List<Orders> selectByPlate(@Param("plateNumber") String plateNumber);

    @Select("SELECT * FROM orders ORDER BY create_time DESC")
    List<Orders> selectAll();

    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);
}