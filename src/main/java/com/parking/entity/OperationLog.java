package com.parking.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private Long operatorId;
    private String operatorName;
    private String operationType;
    private String operationContent;
    private String ipAddress;
    private LocalDateTime createTime;
}