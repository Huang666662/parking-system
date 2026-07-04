package com.parking.entity;

import lombok.Data;
import java.time.LocalTime;

@Data
public class UnloadingChannel {
    private Integer id;
    private String channelName;
    private String channelCode;
    private String spaceIds;
    private LocalTime allowStartTime;
    private LocalTime allowEndTime;
    private Integer isEnabled;
}