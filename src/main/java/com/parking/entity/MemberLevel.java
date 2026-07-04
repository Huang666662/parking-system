package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MemberLevel {
    private Integer id;
    private String levelName;
    private String levelCode;
    private Integer minPoints;
    private BigDecimal discountRate;
    private Integer monthlyFreeMinutes;
}