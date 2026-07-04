package com.parking.service;

import com.parking.entity.SubscriptionPackage;
import java.util.List;

public interface ISubscriptionPackageService {
    SubscriptionPackage getPackage(Integer id);
    List<SubscriptionPackage> listActive();
}