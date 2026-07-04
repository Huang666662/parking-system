package com.parking.service.impl;

import com.parking.entity.Permission;
import com.parking.mapper.PermissionMapper;
import com.parking.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public int addPermission(Permission permission) {
        return permissionMapper.insert(permission);
    }

    @Override
    public int updatePermission(Permission permission) {
        return permissionMapper.update(permission);
    }

    @Override
    public int deletePermission(Integer id) {
        return permissionMapper.deleteById(id);
    }

    @Override
    public Permission getPermission(Integer id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public List<Permission> listAll() {
        return permissionMapper.selectAll();
    }
}