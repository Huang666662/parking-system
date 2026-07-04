package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinancialRecord {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String recordType;
    private LocalDateTime recordTime;
}