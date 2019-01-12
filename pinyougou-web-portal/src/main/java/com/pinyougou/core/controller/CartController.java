package com.pinyougou.core.controller;

import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.core.service.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {


    @Reference
    private CartService cartService;

    /**
     * 将商品加入购物车
     *
     * @param itemId
     * @param num
     * @param request
     * @param response
     * @return
     */
    //	@CrossOrigin(origins={"http://localhost:9003"}, allowCredentials="true")
    // allowCredentials="true"，无需设置，默认为：true
    @CrossOrigin(origins = {"http://localhost:9003"})
    @RequestMapping("/addGoodsToCartList.do")
    public Result addGoodsToCartList(Long itemId, int num, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1、定义空的购物车集合
            List<Cart> cartList = null;
            // 2、判断本地是否有购物车
            //定义一个标识（开关），用来判断本地是否有cookie
            boolean flag = false;
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {             //cookie :   key-value
                    if ("BUYER_CART".equals(cookie.getName())) {
                        //3.有车，取出来赋值给定义的空车集合
                        //将json字符串转成对象
                        cartList = JSON.parseArray(URLDecoder.decode(cookie.getValue(), "utf-8"), Cart.class);
                        flag = true;
                        // 找到购物车了就跳出循环
                        break;
                    }
                }
            }
            // 4、本地没有，说明是第一次，因此需要创建一个购物车集合
            if (cartList == null) {
                cartList = new ArrayList<>();
            }
            // 5、封装页面数据: 注意，对cookie进行瘦身
            Cart cart = new Cart();
            Item item = cartService.findOne(itemId);
            cart.setSellerId(item.getSellerId());
            ArrayList<OrderItem> orderItemList = new ArrayList<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(itemId);        //保存购买的库存id
            orderItem.setNum(num);              //保存购买的数量
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList); //商品购物项
            //6、将商品加入购物车中
            // 6-1、判断该商品是否属于同一个商家：
            int sellerIndexOf = cartList.indexOf(cart);// 判断sellerId是否一样，因此需要重写cart中的equals和hashcode方法
            if (sellerIndexOf != -1) {
                // 属于同一个商家
                // 6-2、判断该商品是否属于同款：判断itemId是否一样，因此需要重写orderItem中的方法
                // 取出该商家数据并判断
                Cart oldCart = cartList.get(sellerIndexOf);
                int itemIndexOf = oldCart.getOrderItemList().indexOf(orderItem);
                //判断是否是用同款商品
                if (itemIndexOf != -1) {
                    // 有同款商品，合并数量
                    OrderItem oldOrderItem = oldCart.getOrderItemList().get(itemIndexOf);
                    oldOrderItem.setNum(oldOrderItem.getNum() + num);
                } else {
                    // 无同款商品，将该购物项添加到已有的购物项集合中
                    oldCart.getOrderItemList().add(orderItem);
                }
            } else {
                // 不属于同一个商家，直接加入购物车
                cartList.add(cart);
            }

            //判断用户是否登录

            //userName:anonymousUser,代表当前的用户未登录
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(userName)) {    //已登录
                //6.1、将购物车保存到redis中
                //将本地cookie中的数据同步到 redis中
                cartService.mergeCartToRedis(userName, cartList);
                //如果本地有购物车，清空本地的购物车
                if (flag) {
                    Cookie cookie = new Cookie("BUYER_CART", null);
                    cookie.setMaxAge(0);    // 设置cookie存活时间
                    cookie.setPath("/");        // 设置cookie共享
                    response.addCookie(cookie);
                }

            } else {     //未登录
                //6.2、将购物车保存起来： Object对象--->json字符串
                Cookie cookie = new Cookie("BUYER_CART", URLEncoder.encode(JSON.toJSONString(cartList), "utf-8"));
                cookie.setMaxAge(60 * 60);    // 设置cookie存活时间
                cookie.setPath("/");        // 设置cookie共享
                response.addCookie(cookie);
            }

            return new Result(true, "加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "加入购物车失败");
        }
    }

    /**
     * 购物车商品回显
     *
     * @return
     */
    @RequestMapping("/findCartList.do")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {

            // 未登录，从cookie取
            List<Cart> cartList = null;
            Cookie[] cookies = request.getCookies();
            // 2、判断本地是否有购物车
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {             //cookie :   key-value
                    if ("BUYER_CART".equals(cookie.getName())) {
                        //String value = cookie.getValue();
                        cartList = JSON.parseArray(URLDecoder.decode(cookie.getValue(), "utf-8"), Cart.class);
                        break;
                    }
                }
            }

        // 已登录，从redis取
        //判断用户是否登录
        //userName:anonymousUser,代表当前的用户未登录
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(userName)) {    //已登录
            // 将本地购物车同步到redis中
            if (cartList != null) {
                cartService.mergeCartToRedis(userName,cartList);
                Cookie cookie = new Cookie("BUYER_CART", null);
                cookie.setMaxAge(0);    // 设置cookie存活时间
                cookie.setPath("/");        // 设置cookie共享
                response.addCookie(cookie);
            }
            cartList = cartService.findCartListFromRedis(userName);
        }


        if (cartList != null) {
            // 需要将页面显示的数据进行填充
            cartList = cartService.findCartList(cartList);
        }
        return cartList;
    }
}
