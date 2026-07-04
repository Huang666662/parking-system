package com.parking.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface VehicleProcedureMapper {

    /**
     * 车辆入场存储过程
     * @param params 需包含 plateNumber, userId; 返回后包含 spaceNumber, recordNo
     */
    void callVehicleEntry(Map<String, Object> params);

    /**
     * 车辆出场计费存储过程
     * @param params 需包含 recordId; 返回后包含 originalFee, discountFee, actualFee
     */
    void callCalculateFee(Map<String, Object> params);

    /**
     * 车辆出场结算存储过程
     * @param params 需包含 recordId, paymentMethod; 返回后包含 orderNo, paymentNo, amount
     */
    void callVehicleExit(Map<String, Object> params);
}
