package com.parking.service.impl;

import com.parking.entity.User;
import com.parking.mapper.UserMapper;
import com.parking.service.IUserService;
import com.parking.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    @Override
    public int addUser(User user) {
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.update(user);
    }

    @Override
    public int deleteUser(Long id) {
        return userMapper.deleteById(id);
    }

    @Override
    public User getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> listUsers(String username) {
        if (username != null && !username.isEmpty()) {
            return userMapper.selectByUsername(username);
        }
        return userMapper.selectAll();
    }
}
