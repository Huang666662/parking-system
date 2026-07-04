package com.parking.service.impl;

import com.parking.entity.WriteOffRecord;
import com.parking.mapper.WriteOffRecordMapper;
import com.parking.service.IWriteOffRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WriteOffRecordServiceImpl implements IWriteOffRecordService {

    @Autowired
    private WriteOffRecordMapper writeOffRecordMapper;

    @Override
    public int addWriteOff(WriteOffRecord record) {
        return writeOffRecordMapper.insert(record);
    }

    @Override
    public WriteOffRecord getWriteOff(Long id) {
        return writeOffRecordMapper.selectById(id);
    }

    @Override
    public List<WriteOffRecord> listByMerchantId(Long merchantId) {
        return writeOffRecordMapper.selectByMerchantId(merchantId);
    }
}