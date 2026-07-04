package com.parking.mapper;

import com.parking.entity.RolePermission;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RolePermissionMapper {
    @Select("SELECT * FROM role_permission WHERE id = #{id}")
    RolePermission selectById(Long id);

    @Insert("INSERT INTO role_permission (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RolePermission rolePermission);

    @Delete("DELETE FROM role_permission WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM role_permission")
    List<RolePermission> selectAll();
}