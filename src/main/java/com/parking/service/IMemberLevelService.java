package com.parking.service;

import com.parking.entity.MemberLevel;
import java.util.List;

public interface IMemberLevelService {
    MemberLevel getLevel(Integer id);
    List<MemberLevel> listAll();
    int addLevel(MemberLevel level);
    int deleteLevel(Integer id);
}