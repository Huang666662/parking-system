package com.parking.service;

import com.parking.entity.BlackList;
import java.util.List;

public interface IBlackListService {
    int addBlackList(BlackList blackList);
    int deleteByPlate(String plateNumber);
    BlackList getByPlate(String plateNumber);
    BlackList getBlackList(Long id);
    List<BlackList> listAll();
}