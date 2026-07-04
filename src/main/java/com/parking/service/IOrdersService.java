package com.parking.service;

import com.parking.entity.Orders;
import java.util.List;

public interface IOrdersService {
    int addOrder(Orders order);
    int updateOrder(Orders order);
    int deleteOrder(Long id);
    Orders getOrder(Long id);
    List<Orders> listOrders(String plateNumber);
}