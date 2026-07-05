package com.parking.service;

import com.parking.entity.ParkingRecord;
import java.util.List;

public interface IParkingRecordService {
    int addRecord(ParkingRecord record);
    int updateRecord(ParkingRecord record);
    ParkingRecord getRecord(Long id);
    ParkingRecord getCurrentByPlate(String plateNumber);
    List<ParkingRecord> listRecords(String plateNumber);
    void enter(String plateNumber, Long userId, Long spaceId);
    void exit(Long recordId, String paymentMethod, Integer usePoints, Long userCouponId);
}