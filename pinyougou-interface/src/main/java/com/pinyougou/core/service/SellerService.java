package com.pinyougou.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {

    /**
     * 商家入驻申请
     * @param seller
     */
    public void add(Seller seller);

    /**
     * 待审核商家的列表查询
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    PageResult search(Integer page, Integer rows, Seller seller);

    /**
     * 回显商家详情
     * @param sellerId
     * @return
     */
    public Seller findOne(String sellerId);

    /**
     * 商家审核(修改商家状态)
     * @param sellerId
     * @param status
     */
    public void updateStatus(String sellerId, String status);
}
