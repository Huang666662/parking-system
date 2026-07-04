package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingRecord {
    private Long id;
    private String recordNo;
    private String plateNumber;
    private Long userId;
    private Long spaceId;
    private LocalDateTime enterTime;
    private LocalDateTime exitTime;
    private Integer durationMinutes;
    private BigDecimal originalFee;
    private BigDecimal discountFee;
    private BigDecimal actualFee;
    private String status;
    private String paymentStatus;
}