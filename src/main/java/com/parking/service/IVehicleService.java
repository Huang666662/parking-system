package com.parking.service;

import com.parking.entity.Vehicle;
import java.util.List;

public interface IVehicleService {
    int addVehicle(Vehicle vehicle);
    int updateVehicle(Vehicle vehicle);
    int deleteVehicle(Long id);
    Vehicle getVehicle(Long id);
    List<Vehicle> listByUserId(Long userId);
}