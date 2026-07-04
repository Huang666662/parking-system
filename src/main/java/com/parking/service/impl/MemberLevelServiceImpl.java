package com.parking.service.impl;

import com.parking.entity.MemberLevel;
import com.parking.mapper.MemberLevelMapper;
import com.parking.service.IMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemberLevelServiceImpl implements IMemberLevelService {

    @Autowired
    private MemberLevelMapper memberLevelMapper;

    @Override
    public MemberLevel getLevel(Integer id) {
        return memberLevelMapper.selectById(id);
    }

    @Override
    public List<MemberLevel> listAll() {
        return memberLevelMapper.selectAll();
    }

    @Override
    public int addLevel(MemberLevel level) {
        return memberLevelMapper.insert(level);
    }

    @Override
    public int deleteLevel(Integer id) {
        return memberLevelMapper.deleteById(id);
    }
}