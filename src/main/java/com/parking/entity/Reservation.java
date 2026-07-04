package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long id;
    private String reservationNo;
    private Long userId;
    private String plateNumber;
    private Long spaceId;
    private LocalDateTime reserveTime;
    private String status;
    private BigDecimal depositAmount;
    private LocalDateTime createTime;
}