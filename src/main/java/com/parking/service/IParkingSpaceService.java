package com.parking.service;

import com.parking.entity.ParkingSpace;
import java.util.List;

public interface IParkingSpaceService {
    int addSpace(ParkingSpace space);
    int updateSpace(ParkingSpace space);
    int deleteSpace(Long id);
    ParkingSpace getSpace(Long id);
    List<ParkingSpace> listAll();
    List<ParkingSpace> listFreeSpaces();
}