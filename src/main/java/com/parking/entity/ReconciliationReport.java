package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReconciliationReport {
    private Long id;
    private LocalDate reportDate;
    private BigDecimal totalIncome;
    private BigDecimal cashIncome;
    private BigDecimal onlineIncome;
    private BigDecimal discountAmount;
    private String status;
    private LocalDateTime createTime;
}