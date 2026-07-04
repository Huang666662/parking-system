package com.parking.service;

import com.parking.entity.MerchantUser;
import java.util.List;

public interface IMerchantUserService {
    int addMerchantUser(MerchantUser user);
    MerchantUser getMerchantUser(Long id);
    List<MerchantUser> listByMerchantId(Long merchantId);
}