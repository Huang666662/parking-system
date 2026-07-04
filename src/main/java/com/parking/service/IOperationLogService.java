package com.parking.service;

import com.parking.entity.OperationLog;
import java.util.List;

public interface IOperationLogService {
    int addLog(OperationLog log);
    OperationLog getLog(Long id);
    List<OperationLog> listAll();
}