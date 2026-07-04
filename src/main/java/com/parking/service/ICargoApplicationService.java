package com.parking.service;

import com.parking.entity.CargoApplication;
import java.util.List;

public interface ICargoApplicationService {
    int addApplication(CargoApplication application);
    int approveApplication(Long id, String status);
    CargoApplication getApplication(Long id);
    List<CargoApplication> listAll();
    List<CargoApplication> listPending();
}