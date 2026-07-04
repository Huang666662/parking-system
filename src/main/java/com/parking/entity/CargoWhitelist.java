package com.parking.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CargoWhitelist {
    private Long id;
    private String plateNumber;
    private Long merchantId;
    private String driverName;
    private String driverPhone;
    private LocalTime allowStartTime;
    private LocalTime allowEndTime;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private String status;
}