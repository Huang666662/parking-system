package com.parking.service.impl;

import com.parking.entity.Admin;
import com.parking.mapper.AdminMapper;
import com.parking.service.IAdminService;
import com.parking.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin login(String username, String password) {
        return adminMapper.login(username, password);
    }

    @Override
    public int addAdmin(Admin admin) {
        admin.setPassword(MD5Util.encrypt(admin.getPassword()));
        return adminMapper.insert(admin);
    }

    @Override
    public int updateAdmin(Admin admin) {
        return adminMapper.update(admin);
    }

    @Override
    public int deleteAdmin(Long id) {
        return adminMapper.deleteById(id);
    }

    @Override
    public Admin getAdmin(Long id) {
        return adminMapper.selectById(id);
    }

    @Override
    public List<Admin> listAdmins(String username) {
        if (username != null && !username.isEmpty()) {
            return adminMapper.selectByUsername(username);
        }
        return adminMapper.selectAll();
    }
}
