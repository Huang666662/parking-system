package com.parking.service;

import com.parking.entity.WriteOffRecord;
import java.util.List;

public interface IWriteOffRecordService {
    int addWriteOff(WriteOffRecord record);
    WriteOffRecord getWriteOff(Long id);
    List<WriteOffRecord> listByMerchantId(Long merchantId);
}