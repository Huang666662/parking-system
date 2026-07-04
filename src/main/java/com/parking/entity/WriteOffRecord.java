package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WriteOffRecord {
    private Long id;
    private Long merchantId;
    private String ticketNo;
    private String plateNumber;
    private Integer freeMinutes;
    private LocalDateTime writeOffTime;
}