package com.parking.mapper;

import com.parking.entity.Reservation;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ReservationMapper {
    @Select("SELECT * FROM reservation WHERE id = #{id}")
    Reservation selectById(Long id);

    @Insert("INSERT INTO reservation (reservation_no, user_id, plate_number, space_id, reserve_time, deposit_amount, create_time) VALUES (#{reservationNo}, #{userId}, #{plateNumber}, #{spaceId}, #{reserveTime}, #{depositAmount}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Reservation reservation);

    @Update("UPDATE reservation SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("SELECT * FROM reservation ORDER BY reserve_time DESC")
    List<Reservation> selectAll();
}