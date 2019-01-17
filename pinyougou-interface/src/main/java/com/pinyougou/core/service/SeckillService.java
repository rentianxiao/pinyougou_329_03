package com.pinyougou.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;

import java.util.List;

public interface SeckillService {

    /**
     * 查询所有秒杀商品
     *
     * @return
     */
    public PageResult findAllGoods(Integer pageNum, Integer pageSize,String sellerId);

    /**
     * 根据商家id查询商家名下的所有商品
     * @return
     */
    public List<Goods> findGoodsName(String sellerId);

    /**
     * 新增秒杀商品(申请)
     * @param seckillGoods
     */
    public void add(SeckillGoods seckillGoods);

    /**
     * 商品回显
     * @return
     */
    public SeckillGoods findOne(Long id);

    /**
     * 修改秒杀商品
     * @param seckillGoods
     */
    public void update(SeckillGoods seckillGoods);

    /**
     * 根据id删除秒杀商品
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 查询所有秒杀商品订单
     * @return
     */
    public List<SeckillOrder> findAllOrder();


}