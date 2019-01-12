package com.pinyougou.core.service;

import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;

import java.util.List;

public interface CartService {

    /**
     * 根据id获取库存
     * @param itemId
     * @return
     */
    public Item findOne(Long itemId);

    /**
     * 购物车商品回显
     * @param cartList
     * @return
     */
    public List<Cart> findCartList(List<Cart> cartList);

    /**
     * 将本地cookie中的数据同步到 redis中
     * @param userName
     * @param cartList
     */
    public  void mergeCartToRedis(String userName, List<Cart> cartList);

    /**
     * 从redis中取出购物车
     * @param name
     * @return
     */
    public List<Cart> findCartListFromRedis(String name);
}
