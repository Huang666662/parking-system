package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRecord {
    private Long id;
    private String paymentNo;
    private Long recordId;
    private BigDecimal amount;
    private String paymentMethod;
    private Integer discountPoints;
    private Long couponId;
    private String status;
    private LocalDateTime paymentTime;
}