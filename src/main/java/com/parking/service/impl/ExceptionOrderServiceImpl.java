package com.parking.service.impl;

import com.parking.entity.ExceptionOrder;
import com.parking.mapper.ExceptionOrderMapper;
import com.parking.service.IExceptionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExceptionOrderServiceImpl implements IExceptionOrderService {

    @Autowired
    private ExceptionOrderMapper exceptionOrderMapper;

    @Override
    public int addExceptionOrder(ExceptionOrder order) {
        return exceptionOrderMapper.insert(order);
    }

    @Override
    public int updateExceptionOrder(ExceptionOrder order) {
        return exceptionOrderMapper.update(order);
    }

    @Override
    public ExceptionOrder getExceptionOrder(Long id) {
        return exceptionOrderMapper.selectById(id);
    }

    @Override
    public List<ExceptionOrder> listPending() {
        return exceptionOrderMapper.selectPending();
    }
}