package com.pinyougou.core.service.orderManage;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;

import java.util.List;

public interface OrderService {
    public PageResult search(Integer page, Integer rows, Order order);

    List<Order> findAll();

    void saveOrder(Order order);
}
