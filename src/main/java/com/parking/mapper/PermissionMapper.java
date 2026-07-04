package com.parking.mapper;

import com.parking.entity.Permission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PermissionMapper {
    @Select("SELECT * FROM permission WHERE id = #{id}")
    Permission selectById(Integer id);

    @Insert("INSERT INTO permission (permission_name, permission_code, parent_id, type, sort_order) VALUES (#{permissionName}, #{permissionCode}, #{parentId}, #{type}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Permission permission);

    @Update("UPDATE permission SET permission_name=#{permissionName}, permission_code=#{permissionCode}, parent_id=#{parentId}, type=#{type}, sort_order=#{sortOrder} WHERE id=#{id}")
    int update(Permission permission);

    @Delete("DELETE FROM permission WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT * FROM permission")
    List<Permission> selectAll();
}