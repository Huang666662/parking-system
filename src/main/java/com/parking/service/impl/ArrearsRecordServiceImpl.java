package com.parking.service.impl;

import com.parking.entity.ArrearsRecord;
import com.parking.mapper.ArrearsRecordMapper;
import com.parking.service.IArrearsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArrearsRecordServiceImpl implements IArrearsRecordService {

    @Autowired
    private ArrearsRecordMapper arrearsRecordMapper;

    @Override
    public int addArrears(ArrearsRecord record) {
        return arrearsRecordMapper.insert(record);
    }

    @Override
    public int incrementRemind(Long id) {
        return arrearsRecordMapper.incrementRemind(id);
    }

    @Override
    public ArrearsRecord getArrears(Long id) {
        return arrearsRecordMapper.selectById(id);
    }

    @Override
    public List<ArrearsRecord> listUnpaid() {
        return arrearsRecordMapper.selectUnpaid();
    }
}