package com.parking.mapper;

import com.parking.entity.Role;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RoleMapper {
    @Select("SELECT * FROM role WHERE id = #{id}")
    Role selectById(Integer id);

    @Insert("INSERT INTO role (role_name, role_code, description, create_time) VALUES (#{roleName}, #{roleCode}, #{description}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);

    @Update("UPDATE role SET role_name=#{roleName}, role_code=#{roleCode}, description=#{description} WHERE id=#{id}")
    int update(Role role);

    @Delete("DELETE FROM role WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM role")
    List<Role> selectAll();
}