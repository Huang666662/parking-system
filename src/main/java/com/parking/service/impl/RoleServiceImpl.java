package com.parking.service.impl;

import com.parking.entity.Role;
import com.parking.mapper.RoleMapper;
import com.parking.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public int addRole(Role role) {
        return roleMapper.insert(role);
    }

    @Override
    public int updateRole(Role role) {
        return roleMapper.update(role);
    }

    @Override
    public int deleteRole(Integer id) {
        return roleMapper.deleteById(id);
    }

    @Override
    public Role getRole(Integer id) {
        return roleMapper.selectById(id);
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.selectAll();
    }
}