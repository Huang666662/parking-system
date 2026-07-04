package com.parking.mapper;

import com.parking.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Select("SELECT id, username, password, phone, points, member_level_id, status FROM user WHERE username = #{username} AND password = #{password} AND status = 1")
    User login(@Param("username") String username, @Param("password") String password);

    @Insert("INSERT INTO user (username, password, phone, points, member_level_id, status, register_time) " +
            "VALUES (#{username}, #{password}, #{phone}, #{points}, #{memberLevelId}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET username=#{username}, phone=#{phone}, points=#{points}, member_level_id=#{memberLevelId}, status=#{status} WHERE id=#{id}")
    int update(User user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM user")
    List<User> selectAll();

    @Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{username}, '%')")
    List<User> selectByUsername(@Param("username") String username);
}