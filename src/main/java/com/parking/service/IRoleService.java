package com.parking.service;

import com.parking.entity.Role;
import java.util.List;

public interface IRoleService {
    int addRole(Role role);
    int updateRole(Role role);
    int deleteRole(Integer id);
    Role getRole(Integer id);
    List<Role> listAll();
}