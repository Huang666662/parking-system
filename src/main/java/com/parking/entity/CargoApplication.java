package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CargoApplication {
    private Long id;
    private String applyNo;
    private String plateNumber;
    private String merchantName;
    private String driverName;
    private String driverPhone;
    private String deliveryImage;
    private Integer permitDuration;
    private String status;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
}