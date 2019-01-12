package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.order.Order;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 提交订单
     * @param order
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody Order order) {
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            orderService.add(order, name);
            return new Result(true, "订单提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "订单提交失败");
        }
    }

}
