package com.parking.service.impl;

import com.parking.entity.CargoWhitelist;
import com.parking.mapper.CargoWhitelistMapper;
import com.parking.service.ICargoWhitelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CargoWhitelistServiceImpl implements ICargoWhitelistService {

    @Autowired
    private CargoWhitelistMapper cargoWhitelistMapper;

    @Override
    public int addWhitelist(CargoWhitelist whitelist) {
        return cargoWhitelistMapper.insert(whitelist);
    }

    @Override
    public int updateStatus(Long id, String status) {
        return cargoWhitelistMapper.updateStatus(id, status);
    }

    @Override
    public CargoWhitelist getWhitelist(Long id) {
        return cargoWhitelistMapper.selectById(id);
    }

    @Override
    public List<CargoWhitelist> listAll() {
        return cargoWhitelistMapper.selectAll();
    }

    @Override
    public List<CargoWhitelist> listByMerchantId(Long merchantId) {
        return cargoWhitelistMapper.selectByMerchantId(merchantId);
    }
}