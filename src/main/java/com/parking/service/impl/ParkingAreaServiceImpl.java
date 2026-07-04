package com.parking.service.impl;

import com.parking.entity.ParkingArea;
import com.parking.mapper.ParkingAreaMapper;
import com.parking.service.IParkingAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParkingAreaServiceImpl implements IParkingAreaService {

    @Autowired
    private ParkingAreaMapper parkingAreaMapper;

    @Override
    public int addArea(ParkingArea area) {
        return parkingAreaMapper.insert(area);
    }

    @Override
    public int updateArea(ParkingArea area) {
        return parkingAreaMapper.update(area);
    }

    @Override
    public int deleteArea(Integer id) {
        return parkingAreaMapper.deleteById(id);
    }

    @Override
    public ParkingArea getArea(Integer id) {
        return parkingAreaMapper.selectById(id);
    }

    @Override
    public List<ParkingArea> listAll() {
        return parkingAreaMapper.selectAll();
    }
}