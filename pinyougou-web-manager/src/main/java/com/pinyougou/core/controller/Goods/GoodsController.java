package com.pinyougou.core.controller.Goods;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.GoodsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 运营商查询查询待审核且未删除的商品列表信息
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        return goodsService.searchByManager(page, rows, goods);
    }

    /**
     * 更新商品的审核状态
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return new Result(true, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "操作失败");
        }
    }

    /**
     * 删除商品（逻辑删除）
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "删除失败");
        }
    }
}
