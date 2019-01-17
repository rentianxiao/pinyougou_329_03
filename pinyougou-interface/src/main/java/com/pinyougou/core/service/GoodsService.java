package com.pinyougou.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.vo.GoodsVo;

import java.util.List;

public interface GoodsService {

    /**
     * 保存商品
     * @param goodsVo
     */
    public void add(GoodsVo goodsVo);

    /**
     * 查询当前商家下的商品列表
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    public PageResult search(Integer page, Integer rows, Goods goods);

    /**
     * 商品回显
     * @param id
     * @return
     */
    public GoodsVo findOne(Long id);

    /**
     * 商品更新
     * @param goodsVo
     */
    public void update(GoodsVo goodsVo);

    /**
     * 运营商查询查询待审核且未删除的商品列表信息
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    public PageResult searchByManager(Integer page, Integer rows, Goods goods);

    /**
     * 更新商品的审核状态
     * @param ids
     * @param Status
     */
    public void updateStatus(Long[] ids, String Status);

    /**
     * 删除商品（逻辑删除）
     * @param ids
     */
    public void delete(Long[] ids);

    List<Goods> findAll();
}
