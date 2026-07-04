package com.parking.service.impl;

import com.parking.entity.SubscriptionPackage;
import com.parking.mapper.SubscriptionPackageMapper;
import com.parking.service.ISubscriptionPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubscriptionPackageServiceImpl implements ISubscriptionPackageService {

    @Autowired
    private SubscriptionPackageMapper subscriptionPackageMapper;

    @Override
    public SubscriptionPackage getPackage(Integer id) {
        return subscriptionPackageMapper.selectById(id);
    }

    @Override
    public List<SubscriptionPackage> listActive() {
        return subscriptionPackageMapper.selectActive();
    }
}