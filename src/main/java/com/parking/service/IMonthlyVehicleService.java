package com.parking.service;

import com.parking.entity.MonthlyVehicle;
import java.util.List;

public interface IMonthlyVehicleService {
    int addVehicle(MonthlyVehicle vehicle);
    int updateStatus(Long id, String status);
    MonthlyVehicle getVehicle(Long id);
    MonthlyVehicle getByPlate(String plateNumber);
    List<MonthlyVehicle> listExpired();
    List<MonthlyVehicle> listAll();
}