package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Role {
    private Integer id;
    private String roleName;
    private String roleCode;
    private String description;
    private LocalDateTime createTime;
}