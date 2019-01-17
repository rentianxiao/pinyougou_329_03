package com.pinyougou.core.service.seckillorder;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillOrder;

import java.util.List;

public interface SeckillOrderService {

    /**
     * 查询所有秒杀订单
     * @return
     */
    public List<SeckillOrder> findAll();

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(Integer pageNum, Integer pageSize);

    public PageResult search(Integer pageNum, Integer pageSize, SeckillOrder seckillOrder);
}
