package com.parking.service;

import com.parking.entity.SpaceType;
import java.util.List;

public interface ISpaceTypeService {
    int addType(SpaceType type);
    int updateType(SpaceType type);
    int deleteType(Integer id);
    SpaceType getType(Integer id);
    List<SpaceType> listAll();
}