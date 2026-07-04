package com.parking.service;

import com.parking.entity.WhiteList;
import java.util.List;

public interface IWhiteListService {
    int addWhiteList(WhiteList whiteList);
    int updateStatusByPlate(String plateNumber, String status);
    WhiteList getActiveByPlate(String plateNumber);
    WhiteList getWhiteList(Long id);
    List<WhiteList> listAll();
}