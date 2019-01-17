package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.SeckillService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Resource
    private SeckillGoodsDao seckillGoodsDao;

    @Resource
    private SeckillOrderDao seckillOrderDao;

    @Resource
    private GoodsDao goodsDao;


    /**
     * 查询所有秒杀商品
     *
     * @return
     */
    @Override
    public PageResult findAllGoods(Integer pageNum, Integer pageSize ,String sellerId) {
        PageHelper.startPage(pageNum, pageSize);
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        seckillGoodsQuery.createCriteria().andSellerIdEqualTo(sellerId);
        Page<SeckillGoods> page = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
        return new PageResult(page.getResult(),page.getTotal());
    }

    /**
     * 查询该商家名下的所有商品
     *
     * @return
     */
    @Override
    public List<Goods> findGoodsName(String sellerId) {
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.createCriteria().andSellerIdEqualTo(sellerId);
        List<Goods> goodsList = goodsDao.selectByExample(goodsQuery);
        return goodsList;
    }


    /**
     * 新增秒杀商品
     * @param
     */
    @Override
    public void add(SeckillGoods seckillGoods) {
        SeckillGoods seckillGood = setSeckillGoods(seckillGoods);
        seckillGoodsDao.updateByPrimaryKeySelective(seckillGood);
    }

    /**
     * 商品回显
     * @return
     */
    @Override
    public SeckillGoods findOne(Long id) {
        return seckillGoodsDao.selectByPrimaryKey(id);
    }

    /**
     * 修改秒杀商品
     * @param
     */
    @Override
    public void update(SeckillGoods seckillGoods) {
        SeckillGoods seckillGood = setSeckillGoods(seckillGoods);
        seckillGoodsDao.updateByPrimaryKeySelective(seckillGood);
    }

    //设置秒杀商品属性
    public SeckillGoods setSeckillGoods(SeckillGoods seckillGoods) {
//        SeckillGoods seckillGoods = new SeckillGoods();
//        seckillGoods.setCostPrice(seckillVo.getCostPrice());            //秒杀商品价格
//        seckillGoods.setPrice(seckillVo.getGoods().getPrice());         //商品原价
//        seckillGoods.setSmallPic(seckillVo.getGoods().getSmallPic());   //商品小图片
//        seckillGoods.setGoodsId(seckillVo.getGoods().getId());          //秒杀商品id
//        seckillGoods.setStartTime(seckillVo.getStartTime());            //起始时间
//        seckillGoods.setEndTime(seckillVo.getEndTime());                //终止时间
//        seckillGoods.setNum(seckillVo.getNum());                        //售卖数量
//        seckillGoods.setStockCount(seckillVo.getStockCount());          //库存
//        seckillGoods.setIntroduction(seckillVo.getIntroduce());         //描述
//        seckillGoods.setSellerId(seckillVo.getGoods().getSellerId());   //卖家id
        Long goodsId = seckillGoods.getGoodsId();
        GoodsQuery goodsQuery = new GoodsQuery();
        goodsQuery.createCriteria().andIdEqualTo(goodsId);
        List<Goods> goodsList = goodsDao.selectByExample(goodsQuery);
        for (Goods goods : goodsList) {
            seckillGoods.setPrice(goods.getPrice());
            seckillGoods.setSmallPic(goods.getSmallPic());
            seckillGoods.setGoodsId(goodsId);
        }
//        //申请时设定状态为0,未审核
        seckillGoods.setCreateTime(new Date());                         //申请时间
        seckillGoods.setStatus("0");                                    //商品状态
        return seckillGoods;
    }

    /**
     * 删除秒杀商品
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
            seckillGoodsQuery.createCriteria().andIdEqualTo(id);
            List<SeckillGoods> seckillGoods = seckillGoodsDao.selectByExample(seckillGoodsQuery);
            for (SeckillGoods seckillGood : seckillGoods) {
                seckillGoodsDao.deleteByExample(seckillGoodsQuery);
            }

        }
    }

    /**
     * 查询秒杀订单
     *
     * @return
     */
    @Override
    public List<SeckillOrder> findAllOrder() {
        return null;
    }
}
