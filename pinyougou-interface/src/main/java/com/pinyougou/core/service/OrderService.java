package com.pinyougou.core.service;

import cn.itcast.core.pojo.order.Order;

import java.util.List;
import java.util.Map;

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

    public List<Map<String, String>> findOne();

    public List findName();

    public List findShop(String name);



}
