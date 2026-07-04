package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemConfig {
    private Integer id;
    private String configKey;
    private String configValue;
    private String description;
    private LocalDateTime updateTime;
}
