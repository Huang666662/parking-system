package com.parking.service;

import com.parking.entity.ExceptionOrder;
import java.util.List;

public interface IExceptionOrderService {
    int addExceptionOrder(ExceptionOrder order);
    int updateExceptionOrder(ExceptionOrder order);
    ExceptionOrder getExceptionOrder(Long id);
    List<ExceptionOrder> listPending();
}