package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private String status;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
}