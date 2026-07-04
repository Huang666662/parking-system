package com.parking.service.impl;

import com.parking.entity.MerchantUser;
import com.parking.mapper.MerchantUserMapper;
import com.parking.service.IMerchantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MerchantUserServiceImpl implements IMerchantUserService {

    @Autowired
    private MerchantUserMapper merchantUserMapper;

    @Override
    public int addMerchantUser(MerchantUser user) {
        return merchantUserMapper.insert(user);
    }

    @Override
    public MerchantUser getMerchantUser(Long id) {
        return merchantUserMapper.selectById(id);
    }

    @Override
    public List<MerchantUser> listByMerchantId(Long merchantId) {
        return merchantUserMapper.selectByMerchantId(merchantId);
    }
}