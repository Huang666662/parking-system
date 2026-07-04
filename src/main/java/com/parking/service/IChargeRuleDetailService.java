package com.parking.service;

import com.parking.entity.ChargeRuleDetail;
import java.util.List;

public interface IChargeRuleDetailService {
    int addDetail(ChargeRuleDetail detail);
    List<ChargeRuleDetail> listByRuleId(Integer ruleId);
}