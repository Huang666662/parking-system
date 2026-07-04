package com.parking.service.impl;

import com.parking.entity.RolePermission;
import com.parking.mapper.RolePermissionMapper;
import com.parking.service.IRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RolePermissionServiceImpl implements IRolePermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public int addRolePermission(RolePermission rp) {
        return rolePermissionMapper.insert(rp);
    }

    @Override
    public int deleteRolePermission(Long id) {
        return rolePermissionMapper.deleteById(id);
    }

    @Override
    public RolePermission getRolePermission(Long id) {
        return rolePermissionMapper.selectById(id);
    }

    @Override
    public List<RolePermission> listAll() {
        return rolePermissionMapper.selectAll();
    }
}