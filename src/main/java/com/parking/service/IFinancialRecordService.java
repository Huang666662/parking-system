package com.parking.service;

import com.parking.entity.FinancialRecord;
import java.util.List;

public interface IFinancialRecordService {
    int addFinancialRecord(FinancialRecord record);
    FinancialRecord getFinancialRecord(Long id);
    List<FinancialRecord> listAll();
}