package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExceptionOrder {
    private Long id;
    private Long orderId;
    private String exceptionType;
    private String exceptionDesc;
    private String handleStatus;
    private String handleResult;
    private LocalDateTime createTime;
}