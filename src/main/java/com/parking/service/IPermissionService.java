package com.parking.service;

import com.parking.entity.Permission;
import java.util.List;

public interface IPermissionService {
    int addPermission(Permission permission);
    int updatePermission(Permission permission);
    int deletePermission(Integer id);
    Permission getPermission(Integer id);
    List<Permission> listAll();
}