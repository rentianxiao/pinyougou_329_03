package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 商家入驻申请
     * @param seller
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody Seller seller){
        try {
            sellerService.add(seller);
            return new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }

    /**
     * 带审核商家的列表查询
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    public PageResult search(Integer page,Integer rows , Seller seller){

        return sellerService.search(page,rows,seller);
    }
}
