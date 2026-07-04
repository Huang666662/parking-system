package com.parking.service.impl;

import com.parking.entity.ChargeRuleDetail;
import com.parking.mapper.ChargeRuleDetailMapper;
import com.parking.service.IChargeRuleDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChargeRuleDetailServiceImpl implements IChargeRuleDetailService {

    @Autowired
    private ChargeRuleDetailMapper chargeRuleDetailMapper;

    @Override
    public int addDetail(ChargeRuleDetail detail) {
        return chargeRuleDetailMapper.insert(detail);
    }

    @Override
    public List<ChargeRuleDetail> listByRuleId(Integer ruleId) {
        return chargeRuleDetailMapper.selectByRuleId(ruleId);
    }
}