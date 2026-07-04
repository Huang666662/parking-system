package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParkingSpace {
    private Long id;
    private String spaceNumber;
    private String spaceName;
    private Integer areaId;
    private Integer spaceTypeId;
    private String status;
    private Integer isEnabled;
    private String locationDesc;
    private String remark;
    private LocalDateTime createTime;
}