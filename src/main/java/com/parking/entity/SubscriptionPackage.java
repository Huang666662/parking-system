package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SubscriptionPackage {
    private Integer id;
    private String packageName;
    private String packageType;
    private BigDecimal price;
    private Integer durationDays;
    private BigDecimal discountRate;
    private Integer isActive;
}