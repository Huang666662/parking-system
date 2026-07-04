package com.parking.service.impl;

import com.parking.entity.Vehicle;
import com.parking.mapper.VehicleMapper;
import com.parking.service.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehicleServiceImpl implements IVehicleService {

    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    public int addVehicle(Vehicle vehicle) {
        return vehicleMapper.insert(vehicle);
    }

    @Override
    public int updateVehicle(Vehicle vehicle) {
        return vehicleMapper.update(vehicle);
    }

    @Override
    public int deleteVehicle(Long id) {
        return vehicleMapper.deleteById(id);
    }

    @Override
    public Vehicle getVehicle(Long id) {
        return vehicleMapper.selectById(id);
    }

    @Override
    public List<Vehicle> listByUserId(Long userId) {
        return vehicleMapper.selectByUserId(userId);
    }
}