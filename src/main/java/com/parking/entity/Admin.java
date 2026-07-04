package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    private String role;
    private Integer status;
    private LocalDateTime lastLoginTime;
}