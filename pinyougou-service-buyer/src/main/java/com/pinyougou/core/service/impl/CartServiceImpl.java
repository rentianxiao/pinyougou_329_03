package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.CartService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private ItemDao itemDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据id获取库存对象
     *
     * @param itemId
     * @return
     */
    @Override
    public Item findOne(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }


    /**
     * 将页面需要回显的数据填充到购物车中
     * @param cartList
     * @return
     */
    public List<Cart> findCartList(List<Cart> cartList) {
        // 将页面需要展示的数据填充进来
        for (Cart cart : cartList) {
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                cart.setSellerName(item.getSeller());   // 商家店铺名称
                orderItem.setPicPath(item.getImage());  // 商品图片
                orderItem.setPrice(item.getPrice());    // 商品单价
                orderItem.setTitle(item.getTitle());    // 商品标题
                BigDecimal totalFee = new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum());
                orderItem.setTotalFee(totalFee);        // 商品总价
            }
        }
        return cartList;
    }

    /**
     * 将本地cookie中的购物车数据同步到 redis中
     *
     * @param userName
     * @param newCartList
     */
    @Override
    public void mergeCartToRedis(String userName, List<Cart> newCartList) {
        //1、从redis中取出老购物车车的数据
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("buyerCart").get(userName);
        //2、将新车合并到老车中
        oldCartList = mergeNewCartListtoOldCartList(newCartList, oldCartList);
        //3、将老车重新保存到redis中
        redisTemplate.boundHashOps("buyerCart").put(userName, oldCartList);
    }

    /**
     * 从redis中取出购物车
     * @param name
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String name) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("buyerCart").get(name);
        return cartList;
    }

    //将新车合并到老车中
    private List<Cart> mergeNewCartListtoOldCartList(List<Cart> newCartList, List<Cart> oldCartList) {
        if (newCartList != null) {
            if (oldCartList != null) {
                //新车老车都不为空，进行数据合并
                //将新车合并到老车中
                for (Cart newCart : newCartList) {
                    //判断是否是属于同一商家
                    int sellerIndexof = oldCartList.indexOf(newCart);
                    if (sellerIndexof != -1) {
                        // 判断是否是属于同一款商品
                        List<OrderItem> newOrderItemList = newCart.getOrderItemList(); // 新车购物项
                        List<OrderItem> oldOrderItemList = oldCartList.get(sellerIndexof).getOrderItemList(); //老车购物项
                        // 遍历新车购物项
                        for (OrderItem newOrderItem : newOrderItemList) {
                            // 同一个商家：继续判断是否是同款商品
                            int itemIndexOf = oldOrderItemList.indexOf(newOrderItem);
                            if (itemIndexOf != -1) {
                                // 同款商品
                                OrderItem oldOrderItem = oldOrderItemList.get(itemIndexOf);
                                //合并数量
                                oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
                            } else {
                                // 同商家但不是同款商品，直接将该商品放入老车购物项中
                                oldOrderItemList.add(newOrderItem);
                            }
                        }
                    } else {
                        // 不同商家，将cart放入老车中
                        oldCartList.add(newCart);
                    }
                }
            } else {
                // 老车为空，说明是第一次登录，因此不需要做任何处理，直接返回新车
                return newCartList;
            }
        } else {
            // 新车为空，不需要做任何处理，直接返回老车
            return oldCartList;
        }
        return oldCartList;
    }
}
