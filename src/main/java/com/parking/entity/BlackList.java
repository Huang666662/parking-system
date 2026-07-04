package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlackList {
    private Long id;
    private String plateNumber;
    private String reason;
    private Long operatorId;
    private LocalDateTime createTime;
}