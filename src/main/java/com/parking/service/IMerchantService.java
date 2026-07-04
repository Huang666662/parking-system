package com.parking.service;

import com.parking.entity.Merchant;
import java.util.List;

public interface IMerchantService {
    int addMerchant(Merchant merchant);
    int updateMerchant(Merchant merchant);
    int deleteMerchant(Long id);
    Merchant getMerchant(Long id);
    List<Merchant> listAll();
}