package com.pinyougou.core.controller.Seller;

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
     * 待审核商家的列表查询
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller){
        return sellerService.search(page, rows, seller);
    }


    /**
     * 回显商家详情
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }

    /**
     *商家审核(修改商家状态)
     * @param sellerId
     * @param status
     */
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(String sellerId, String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"操作失败");
        }
    }
}
