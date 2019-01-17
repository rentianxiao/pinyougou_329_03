package com.pinyougou.core.service.impl.seckillgoods;

import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.seckillgoods.SeckillGoodsService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Resource
    private SeckillGoodsDao seckillGoodsDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询所有秒杀商品
     * @return
     */
    @Override
    public List<SeckillGoods> findAll() {
        return seckillGoodsDao.selectByExample(null);
    }

    /**
     * 运营商查询秒杀商品列表信息
     * @param page
     * @param rows
     * @param seckillGoods
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {

        //查询时,将数据写入缓存
        List<SeckillGoods> list = seckillGoodsDao.selectByExample(null);
        if (list != null && list.size() > 0) {
            for (SeckillGoods goods : list) {
                    redisTemplate.boundHashOps("seckillGoods").put(goods.getId(), goods);
            }
        }

        //设置分页查询
        PageHelper.startPage(page, rows);

        //设置查询信息
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();

        //1.查询未审核的商品
        if (seckillGoods.getStatus() != null && !"".equals(seckillGoods.getStatus().trim())) {
            criteria.andStatusEqualTo(seckillGoods.getStatus().trim());
        }
        //2.查询未删除的商品
        //criteria.andIsDeleteIsNull();

        //查询
        Page<SeckillGoods> p = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
        return new PageResult(p.getResult(), p.getTotal());
    }

    /**
     * 运营商审核秒杀商品
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            SeckillGoods seckillGoods = new SeckillGoods();
            seckillGoods.setStatus(status);
            for (Long id : ids) {
                seckillGoods.setId(id);
                seckillGoods.setCheckTime(new Date());
                seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
            }
        }
    }


}
