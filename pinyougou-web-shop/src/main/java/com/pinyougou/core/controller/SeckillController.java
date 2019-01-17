package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.SeckillService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Reference
    private SeckillService seckillService;

    /**
     * 查询本商家的所有商品
     *
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Goods> findAll() {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Goods> goodsList = seckillService.findGoodsName(sellerId);
        return goodsList;
    }



    /**
     * 查询该商家所有秒杀商品
     *
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer pageNum, Integer pageSize) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return seckillService.findAllGoods(pageNum, pageSize,sellerId);
    }

    /**
     * 新增秒杀商品
     *
     * @param seckillGoods
     */
    @RequestMapping("/add")
    public Result add(@RequestBody SeckillGoods seckillGoods) {
        try {
            seckillService.add(seckillGoods);
            return new Result(true, "秒杀商品增加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "秒杀商品增加失败");
        }

    }

    /**
     * 回显需要修改的秒杀商品
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public SeckillGoods findOne(Long id) {
        return seckillService.findOne(id);

    }

    /**
     * 修改秒杀商品
     * @param seckillGoods
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SeckillGoods seckillGoods) {
        try {
            seckillService.update(seckillGoods);
            return new Result(true, "秒杀商品修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "秒杀商品修改失败");
        }
    }


    /**
     * 删除秒杀商品
     * @param ids
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillService.delete(ids);
            return new Result(true, "秒杀商品删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "秒杀商品删除失败");
        }
    }

}
