package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Vehicle {
    private Long id;
    private Long userId;
    private String plateNumber;
    private Integer vehicleTypeId;
    private Integer isDefault;
    private Integer status;
    private LocalDateTime bindTime;
}