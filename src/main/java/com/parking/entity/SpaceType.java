package com.parking.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SpaceType {
    private Integer id;
    private String typeName;
    private BigDecimal extraFeeRate;
    private String icon;
}