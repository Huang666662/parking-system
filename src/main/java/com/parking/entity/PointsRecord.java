package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsRecord {
    private Long id;
    private Long userId;
    private Integer points;
    private String type;
    private String description;
    private LocalDateTime createTime;
}
