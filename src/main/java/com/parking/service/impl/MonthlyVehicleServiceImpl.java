package com.parking.service.impl;

import com.parking.entity.MonthlyVehicle;
import com.parking.mapper.MonthlyVehicleMapper;
import com.parking.service.IMonthlyVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MonthlyVehicleServiceImpl implements IMonthlyVehicleService {

    @Autowired
    private MonthlyVehicleMapper monthlyVehicleMapper;

    @Override
    public int addVehicle(MonthlyVehicle vehicle) {
        return monthlyVehicleMapper.insert(vehicle);
    }

    @Override
    public int updateStatus(Long id, String status) {
        return monthlyVehicleMapper.updateStatus(id, status);
    }

    @Override
    public MonthlyVehicle getVehicle(Long id) {
        return monthlyVehicleMapper.selectById(id);
    }

    @Override
    public MonthlyVehicle getByPlate(String plateNumber) {
        return monthlyVehicleMapper.selectByPlate(plateNumber);
    }

    @Override
    public List<MonthlyVehicle> listExpired() {
        return monthlyVehicleMapper.selectExpired();
    }
}