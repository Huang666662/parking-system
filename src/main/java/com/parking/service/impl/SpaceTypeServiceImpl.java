package com.parking.service.impl;

import com.parking.entity.SpaceType;
import com.parking.mapper.SpaceTypeMapper;
import com.parking.service.ISpaceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SpaceTypeServiceImpl implements ISpaceTypeService {

    @Autowired
    private SpaceTypeMapper spaceTypeMapper;

    @Override
    public int addType(SpaceType type) {
        return spaceTypeMapper.insert(type);
    }

    @Override
    public int updateType(SpaceType type) {
        return spaceTypeMapper.update(type);
    }

    @Override
    public int deleteType(Integer id) {
        return spaceTypeMapper.deleteById(id);
    }

    @Override
    public SpaceType getType(Integer id) {
        return spaceTypeMapper.selectById(id);
    }

    @Override
    public List<SpaceType> listAll() {
        return spaceTypeMapper.selectAll();
    }
}