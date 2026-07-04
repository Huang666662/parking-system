package com.parking.service.impl;

import com.parking.entity.WhiteList;
import com.parking.mapper.WhiteListMapper;
import com.parking.service.IWhiteListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WhiteListServiceImpl implements IWhiteListService {

    @Autowired
    private WhiteListMapper whiteListMapper;

    @Override
    public int addWhiteList(WhiteList whiteList) {
        return whiteListMapper.insert(whiteList);
    }

    @Override
    public int updateStatusByPlate(String plateNumber, String status) {
        return whiteListMapper.updateStatusByPlate(plateNumber, status);
    }

    @Override
    public WhiteList getActiveByPlate(String plateNumber) {
        return whiteListMapper.selectActiveByPlate(plateNumber);
    }

    @Override
    public WhiteList getWhiteList(Long id) {
        return whiteListMapper.selectById(id);
    }

    @Override
    public List<WhiteList> listAll() {
        return whiteListMapper.selectAll();
    }
}