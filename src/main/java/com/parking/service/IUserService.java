package com.parking.service;

import com.parking.entity.User;
import java.util.List;

public interface IUserService {
    User login(String username, String password);
    int addUser(User user);
    int updateUser(User user);
    int deleteUser(Long id);
    User getUser(Long id);
    List<User> listUsers(String username);
}