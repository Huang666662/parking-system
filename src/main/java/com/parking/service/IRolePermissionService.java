package com.parking.service;

import com.parking.entity.RolePermission;
import java.util.List;

public interface IRolePermissionService {
    int addRolePermission(RolePermission rp);
    int deleteRolePermission(Long id);
    RolePermission getRolePermission(Long id);
    List<RolePermission> listAll();
}