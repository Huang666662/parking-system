package com.parking.service.impl;

import com.parking.entity.FinancialRecord;
import com.parking.mapper.FinancialRecordMapper;
import com.parking.service.IFinancialRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FinancialRecordServiceImpl implements IFinancialRecordService {

    @Autowired
    private FinancialRecordMapper financialRecordMapper;

    @Override
    public int addFinancialRecord(FinancialRecord record) {
        return financialRecordMapper.insert(record);
    }

    @Override
    public FinancialRecord getFinancialRecord(Long id) {
        return financialRecordMapper.selectById(id);
    }

    @Override
    public List<FinancialRecord> listAll() {
        return financialRecordMapper.selectAll();
    }
}