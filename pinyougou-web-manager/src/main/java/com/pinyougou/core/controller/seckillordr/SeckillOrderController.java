package com.pinyougou.core.controller.seckillordr;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.seckillorder.SeckillOrderService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 查询所有秒杀订单
     *
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<SeckillOrder> findAll() {
        return seckillOrderService.findAll();
    }

    /**
     * 分页查询秒杀订单
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/findPage.do")
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        return seckillOrderService.findPage(pageNum, pageSize);
    }

    @RequestMapping("/search.do")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody SeckillOrder seckillOrder) {
        return seckillOrderService.search(pageNum, pageSize, seckillOrder);
    }
}
