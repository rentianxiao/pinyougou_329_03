package com.pinyougou.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;

import java.util.List;

public interface OrderService {

    /**
     * 提交订单
     * @param order
     * @param name
     */
    public void add(Order order,String name);

    /**
     * 查询当前用户所有订单
     * @param userName
     */
    public List<Order> findAll(String  userName);


}
