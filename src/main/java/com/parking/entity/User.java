package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private Integer points;
    private Integer memberLevelId;
    private Integer status;
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
}