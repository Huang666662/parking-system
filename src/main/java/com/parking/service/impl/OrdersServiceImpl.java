package com.parking.service.impl;

import com.parking.entity.Orders;
import com.parking.mapper.OrdersMapper;
import com.parking.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrdersServiceImpl implements IOrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public int addOrder(Orders order) {
        return ordersMapper.insert(order);
    }

    @Override
    public int updateOrder(Orders order) {
        return ordersMapper.update(order);
    }

    @Override
    public int deleteOrder(Long id) {
        return ordersMapper.deleteById(id);
    }

    @Override
    public Orders getOrder(Long id) {
        return ordersMapper.selectById(id);
    }

    @Override
    public List<Orders> listOrders(String plateNumber) {
        if (plateNumber != null && !plateNumber.isEmpty()) {
            return ordersMapper.selectByPlate(plateNumber);
        }
        return ordersMapper.selectAll();
    }
}