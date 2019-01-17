package com.pinyougou.core.service.impl.seckillorder;

import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.seckillorder.SeckillOrderService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Resource
    private SeckillOrderDao seckillOrderDao;

    /**
     * 查询所有秒杀订订单
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderDao.selectByExample(null);
    }

    /**
     * 分页查询秒杀订单
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        //查询结果集
        Page<SeckillOrder> page = (Page<SeckillOrder>) seckillOrderDao.selectByExample(null);

        //返回PageResult
        return new PageResult(page.getResult(), page.getTotal());
    }

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, SeckillOrder seckillOrder) {
        PageHelper.startPage(pageNum, pageSize);

        //设置查询条件

        Page<SeckillOrder> page = (Page<SeckillOrder>) seckillOrderDao.selectByExample(null);
        return new PageResult(page.getResult(), page.getTotal());
    }
}
