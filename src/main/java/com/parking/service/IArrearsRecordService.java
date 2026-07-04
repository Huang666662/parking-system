package com.parking.service;

import com.parking.entity.ArrearsRecord;
import java.util.List;

public interface IArrearsRecordService {
    int addArrears(ArrearsRecord record);
    int incrementRemind(Long id);
    ArrearsRecord getArrears(Long id);
    List<ArrearsRecord> listUnpaid();
}