package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ChargeRule {
    private Integer id;
    private String ruleName;
    private String ruleType;
    private BigDecimal unitPrice;
    private Integer freeMinutes;
    private BigDecimal capPrice;
    private Integer isActive;
    private LocalDate effectiveDate;
    private LocalDate expireDate;
}