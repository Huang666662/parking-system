package com.parking.service.impl;

import com.parking.entity.CargoApplication;
import com.parking.mapper.CargoApplicationMapper;
import com.parking.service.ICargoApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CargoApplicationServiceImpl implements ICargoApplicationService {

    @Autowired
    private CargoApplicationMapper cargoApplicationMapper;

    @Override
    public int addApplication(CargoApplication application) {
        return cargoApplicationMapper.insert(application);
    }

    @Override
    public int approveApplication(Long id, String status) {
        return cargoApplicationMapper.approve(id, status);
    }

    @Override
    public CargoApplication getApplication(Long id) {
        return cargoApplicationMapper.selectById(id);
    }

    @Override
    public List<CargoApplication> listAll() {
        return cargoApplicationMapper.selectAll();
    }

    @Override
    public List<CargoApplication> listPending() {
        return cargoApplicationMapper.selectPending();
    }
}