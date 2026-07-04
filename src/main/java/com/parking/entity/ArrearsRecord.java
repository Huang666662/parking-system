package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ArrearsRecord {
    private Long id;
    private Long orderId;
    private String plateNumber;
    private BigDecimal arrearsAmount;
    private String status;
    private Integer remindCount;
    private LocalDateTime createTime;
}