package com.pinyougou.core.controller.order;

import cn.itcast.core.pojo.order.Order;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 查询用户所有订单
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Order> findAll() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.findAll(userName);
    }

}
