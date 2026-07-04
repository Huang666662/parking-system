package com.parking.service.impl;

import com.parking.entity.OperationLog;
import com.parking.mapper.OperationLogMapper;
import com.parking.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OperationLogServiceImpl implements IOperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public int addLog(OperationLog log) {
        return operationLogMapper.insert(log);
    }

    @Override
    public OperationLog getLog(Long id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    public List<OperationLog> listAll() {
        return operationLogMapper.selectAll();
    }
}