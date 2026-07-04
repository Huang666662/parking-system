package com.parking.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MonthlyVehicle {
    private Long id;
    private Long userId;
    private String plateNumber;
    private Integer packageId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createTime;
}