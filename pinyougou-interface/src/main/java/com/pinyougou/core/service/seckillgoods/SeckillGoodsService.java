package com.pinyougou.core.service.seckillgoods;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * 查询所有商品
     * @return
     */
    public List<SeckillGoods> findAll();

    /**
     * 运营商查询秒杀商品信息列表
     * @param page
     * @param rows
     * @param seckillGoods
     * @return
     */
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    /**
     * 运营商审核秒杀商品
     * @param ids
     * @param status
     */
    public void updateStatus(Long[] ids, String status);
}
