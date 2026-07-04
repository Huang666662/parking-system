package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Long id;
    private String couponName;
    private String couponType;
    private BigDecimal discountValue;
    private BigDecimal minAmount;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private LocalDateTime endTime;
    private Integer status;
}