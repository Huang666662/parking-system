package com.parking.service.impl;

import com.parking.entity.Merchant;
import com.parking.mapper.MerchantMapper;
import com.parking.service.IMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MerchantServiceImpl implements IMerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public int addMerchant(Merchant merchant) {
        return merchantMapper.insert(merchant);
    }

    @Override
    public int updateMerchant(Merchant merchant) {
        return merchantMapper.update(merchant);
    }

    @Override
    public int deleteMerchant(Long id) {
        return merchantMapper.deleteById(id);
    }

    @Override
    public Merchant getMerchant(Long id) {
        return merchantMapper.selectById(id);
    }

    @Override
    public List<Merchant> listAll() {
        return merchantMapper.selectAll();
    }
}