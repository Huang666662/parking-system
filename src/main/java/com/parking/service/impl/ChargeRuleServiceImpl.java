package com.parking.service.impl;

import com.parking.entity.ChargeRule;
import com.parking.mapper.ChargeRuleMapper;
import com.parking.service.IChargeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChargeRuleServiceImpl implements IChargeRuleService {

    @Autowired
    private ChargeRuleMapper chargeRuleMapper;

    @Override
    public int addRule(ChargeRule rule) {
        return chargeRuleMapper.insert(rule);
    }

    @Override
    public int updateRule(ChargeRule rule) {
        return chargeRuleMapper.update(rule);
    }

    @Override
    public ChargeRule getRule(Integer id) {
        return chargeRuleMapper.selectById(id);
    }

    @Override
    public int deleteRule(Integer id) {
        return chargeRuleMapper.deleteById(id);
    }

    @Override
    public List<ChargeRule> listActive() {
        return chargeRuleMapper.selectActive();
    }
}