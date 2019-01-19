package com.pinyougou.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderType;
import cn.itcast.core.pojo.order.SellerOrder;
import cn.itcast.core.pojo.vo.SellerOrderVo;

import java.text.ParseException;
import java.util.List;

public interface SellerOrderService {

    public List<SellerOrder> findOrderBySellerId(String sellerId);

    void updateStatus(Long[] ids, String status);

    List<SellerOrderVo> selectByStatusAndData(String sellerId ,String status,String dateLimit) throws ParseException;
}
