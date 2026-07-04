package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Merchant {
    private Long id;
    private String merchantName;
    private String merchantCode;
    private String contactPerson;
    private String contactPhone;
    private BigDecimal settlementRate;
    private Integer status;
    private LocalDateTime createTime;
}