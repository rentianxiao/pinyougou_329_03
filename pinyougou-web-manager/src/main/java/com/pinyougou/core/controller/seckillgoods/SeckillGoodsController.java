package com.pinyougou.core.controller.seckillgoods;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.seckillgoods.SeckillGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询所有秒杀商品
     *
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<SeckillGoods> findAll() {
        return seckillGoodsService.findAll();
    }

    /**
     * 运营商查询秒杀商品列表
     *
     * @param page
     * @param rows
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        return seckillGoodsService.search(page, rows, seckillGoods);
    }

    @RequestMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids, String status){
        try {
            seckillGoodsService.updateStatus(ids, status);
            return new Result(true, "审核通过");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "审核失败");
        }
    }
}
