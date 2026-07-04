package com.parking.service.impl;

import com.parking.entity.BlackList;
import com.parking.mapper.BlackListMapper;
import com.parking.service.IBlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BlackListServiceImpl implements IBlackListService {

    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    public int addBlackList(BlackList blackList) {
        return blackListMapper.insert(blackList);
    }

    @Override
    public int deleteByPlate(String plateNumber) {
        return blackListMapper.deleteByPlate(plateNumber);
    }

    @Override
    public BlackList getByPlate(String plateNumber) {
        return blackListMapper.selectByPlate(plateNumber);
    }

    @Override
    public BlackList getBlackList(Long id) {
        return blackListMapper.selectById(id);
    }

    @Override
    public List<BlackList> listAll() {
        return blackListMapper.selectAll();
    }
}