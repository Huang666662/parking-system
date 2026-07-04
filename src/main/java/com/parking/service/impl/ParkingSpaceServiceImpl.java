package com.parking.service.impl;

import com.parking.entity.ParkingSpace;
import com.parking.mapper.ParkingSpaceMapper;
import com.parking.service.IParkingSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ParkingSpaceServiceImpl implements IParkingSpaceService {

    @Autowired
    private ParkingSpaceMapper parkingSpaceMapper;

    @Override
    public int addSpace(ParkingSpace space) {
        return parkingSpaceMapper.insert(space);
    }

    @Override
    public int updateSpace(ParkingSpace space) {
        return parkingSpaceMapper.update(space);
    }

    @Override
    public int deleteSpace(Long id) {
        return parkingSpaceMapper.deleteById(id);
    }

    @Override
    public ParkingSpace getSpace(Long id) {
        return parkingSpaceMapper.selectById(id);
    }

    @Override
    public List<ParkingSpace> listAll() {
        return parkingSpaceMapper.selectAll();
    }

    @Override
    public List<ParkingSpace> listFreeSpaces() {
        return parkingSpaceMapper.selectFreeSpaces();
    }
}