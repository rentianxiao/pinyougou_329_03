package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderType;
import cn.itcast.core.pojo.order.SellerOrder;
import cn.itcast.core.pojo.vo.SellerOrderVo;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.OrderService;
import com.pinyougou.core.service.SellerOrderService;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/order")
public class SellerOrderController {

    @Reference
    private SellerOrderService sellerOrderService;

    @RequestMapping("/findOrderBySellerId.do")
    public List<SellerOrder> findOrderBySellerId(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SellerOrder> orderList = sellerOrderService.findOrderBySellerId(name);
//        System.out.println(orderList);
        return orderList;
    }

    @RequestMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids,String status){
        try {
            sellerOrderService.updateStatus(ids,status);
            return new Result(true,"成功发货!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"发货失败或交易未完成!");
        }
    }

    @RequestMapping("/selectByStatusAndData.do")
    public List<SellerOrderVo> selectByStatusAndData(String status,String dateLimit) throws ParseException {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("undefined".equals(status) &&  "undefined".equals(dateLimit)) {
            status = null;
            dateLimit = null;
            System.out.println(status + "  " + dateLimit);
            List<SellerOrderVo> sellerOrderVoList = sellerOrderService.selectByStatusAndData(sellerId, status, dateLimit);
            System.out.println(sellerOrderVoList);
            return sellerOrderVoList;

        } else {
            System.out.println(status + "  " + dateLimit);
            List<SellerOrderVo> sellerOrderVoList = sellerOrderService.selectByStatusAndData(sellerId, status, dateLimit);
            System.out.println(sellerOrderVoList);
            return sellerOrderVoList;
        }

    }
}
