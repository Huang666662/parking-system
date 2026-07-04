package com.parking.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class WhiteList {
    private Long id;
    private String plateNumber;
    private Long userId;
    private LocalDate expireDate;
    private String status;
}