package com.parking.service;

import com.parking.entity.CargoWhitelist;
import java.util.List;

public interface ICargoWhitelistService {
    int addWhitelist(CargoWhitelist whitelist);
    int updateStatus(Long id, String status);
    CargoWhitelist getWhitelist(Long id);
    List<CargoWhitelist> listAll();
    List<CargoWhitelist> listByMerchantId(Long merchantId);
}