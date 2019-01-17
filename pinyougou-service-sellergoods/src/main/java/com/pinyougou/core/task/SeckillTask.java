package com.pinyougou.core.task;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SeckillGoodsDao seckillGoodsDao;



    /**
     * 秒杀商品加入缓存
     */
    @Scheduled(cron = " * 0 * * * ?")
    public void refreshSeckillGood() {
        System.out.println("执行了任务调度" + new Date());
        //查询所有秒杀商品键集合
        List ids = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        //查询正在秒杀的商品列表
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        criteria.andStockCountGreaterThan(0);//剩余库存大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andIdNotIn(ids);
        List<SeckillGoods> seckillGoodsList = seckillGoodsDao.selectByExample(seckillGoodsQuery);
        //装入缓存
        for (SeckillGoods seckill : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckill.getId(), seckill);
        }
        System.out.println("将" + seckillGoodsList.size() + "条商品装入缓存");
    }

    /**
     * *
     * 移除秒杀商品
     */
    @Scheduled(cron = "* 0 * * * ?")
    public void removeSeckillGoods() {
        System.out.println("移除秒杀商品任务正在执行");
        //扫描缓存中秒杀商品列表,发现过期的移除
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for (SeckillGoods seckill : seckillGoodsList) {
            //结束日期小于当前日期的毫秒数,表示秒杀过期
            if (seckill.getEndTime().getTime() < System.currentTimeMillis()) {
                seckillGoodsDao.updateByPrimaryKey(seckill);//向数据库保存记录
                redisTemplate.boundHashOps("seckillGoods").delete(seckill.getId());//移除缓存数据
                System.out.println("移除秒杀商品" + seckill.getId());
            }
        }
        System.out.println("移除秒杀商品任务结束");
    }
}
