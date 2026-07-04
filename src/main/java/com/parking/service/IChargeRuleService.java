package com.parking.service;

import com.parking.entity.ChargeRule;
import java.util.List;

public interface IChargeRuleService {
    int addRule(ChargeRule rule);
    int updateRule(ChargeRule rule);
    int deleteRule(Integer id);
    ChargeRule getRule(Integer id);
    List<ChargeRule> listActive();
}