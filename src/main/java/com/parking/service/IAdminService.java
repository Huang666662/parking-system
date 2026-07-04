package com.parking.service;

import com.parking.entity.Admin;
import java.util.List;

public interface IAdminService {
    Admin login(String username, String password);
    int addAdmin(Admin admin);
    int updateAdmin(Admin admin);
    int deleteAdmin(Long id);
    Admin getAdmin(Long id);
    List<Admin> listAdmins(String username);
}