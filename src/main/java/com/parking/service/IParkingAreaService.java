package com.parking.service;

import com.parking.entity.ParkingArea;
import java.util.List;

public interface IParkingAreaService {
    int addArea(ParkingArea area);
    int updateArea(ParkingArea area);
    int deleteArea(Integer id);
    ParkingArea getArea(Integer id);
    List<ParkingArea> listAll();
}