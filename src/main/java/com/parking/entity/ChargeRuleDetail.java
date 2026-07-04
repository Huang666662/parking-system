package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class ChargeRuleDetail {
    private Long id;
    private Integer ruleId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal unitPrice;
}