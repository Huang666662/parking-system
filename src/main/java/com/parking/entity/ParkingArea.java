package com.parking.entity;

import lombok.Data;

@Data
public class ParkingArea {
    private Integer id;
    private String areaName;
    private Integer floor;
    private Integer totalSpaces;
    private String description;
}