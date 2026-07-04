package com.parking.mapper;

import com.parking.entity.Admin;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AdminMapper {
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin selectById(Long id);

    @Insert("INSERT INTO admin (username, password, role, status, last_login_time) VALUES (#{username}, #{password}, #{role}, #{status}, #{lastLoginTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Update("UPDATE admin SET username=#{username}, password=#{password}, role=#{role}, status=#{status}, last_login_time=#{lastLoginTime} WHERE id=#{id}")
    int update(Admin admin);

    @Delete("DELETE FROM admin WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM admin")
    List<Admin> selectAll();

    @Select("SELECT * FROM admin WHERE username LIKE CONCAT('%', #{username}, '%')")
    List<Admin> selectByUsername(@Param("username") String username);

    @Select("SELECT id, username, password, role, status FROM admin WHERE username = #{username} AND password = #{password} AND status = 1")
    Admin login(@Param("username") String username, @Param("password") String password);
}