package com.parking.entity;

import lombok.Data;

@Data
public class MerchantUser {
    private Long id;
    private Long merchantId;
    private String username;
    private String password;
    private String role;
    private Integer status;
}