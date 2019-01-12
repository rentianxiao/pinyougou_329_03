package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ItemDao itemDao;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private PayLogDao payLogDao;

    /**
     * 提交订单
     * @param order
     * @param name
     */
    @Transactional
    public void add(Order order, String name) {
        //一个商家对应一个订单
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("buyerCart").get(name);
        if (cartList != null && cartList.size() > 0) {
            double logtotalFee = 0f;                    // 订单保存本次订单的总金额
            ArrayList<Long> orderList = new ArrayList<>();  // 用来保存所有的订单号
            for (Cart cart : cartList) {
                double payMent = 0f;                    // 该商家下订单的支付金额
                // 保存订单信息,以商家为单位
                long orderId = idWorker.nextId();

                orderList.add(orderId);

                order.setOrderId(orderId);              // 订单id
                order.setPaymentType("1");              // 在线支付
                order.setStatus("1");                   // 支付状态 ：未付款
                order.setCreateTime(new Date());        // 订单创建日期
                order.setUpdateTime(new Date());        // 订单更新日期
                order.setUserId(name);                  // 提交订单用户
                order.setSourceType("2");               // 订单来源：pc端
                order.setSellerId(cart.getSellerId());  // 商家id

                // 获取订单明细
                List<OrderItem> orderItemList = cart.getOrderItemList();
                if (orderItemList != null && orderItemList.size() >  0) {
                    for (OrderItem orderItem : orderItemList) {
                        // 保存订单明细信息
                        long id = idWorker.nextId();
                        orderItem.setId(id); // 订单明细id
                        orderItem.setOrderId(orderId); // 订单id
                        Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                        orderItem.setGoodsId(item.getGoodsId());      // 商品id
                        orderItem.setPicPath(item.getImage());        // 商品图片
                        orderItem.setSellerId(item.getSellerId());    // 商家id
                        orderItem.setTitle(item.getTitle());          // 商品标题
                        // 单价
                        orderItem.setPrice(item.getPrice());
                        // 总价
                        double totalFee = item.getPrice().doubleValue() * orderItem.getNum();
                        orderItem.setTotalFee(new BigDecimal(totalFee));
                        // 订单总价格
                        payMent += totalFee;

                        orderItemDao.insertSelective(orderItem);
                    }
                }
                // 支付总金额
                logtotalFee += payMent;
                order.setPayment(new BigDecimal(payMent)); // 订单支付金额：该商家下所有商品金额

                //保存订单
                orderDao.insertSelective(order);
            }
            // 生成交易日志
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));    // 交易订单号
            payLog.setCreateTime(new Date());                           // 支付日志的创建日期
            payLog.setTotalFee((long)logtotalFee * 100);                // 支付总金额
            payLog.setUserId(name);                                     // 订单用户
            payLog.setTradeState("0");                                  // 未支付
            //[1,2,3,.....]
            payLog.setOrderList(orderList.toString().replace("[","").replace("]",""));
            payLog.setPayType("1");                                     //支付类型：在线支付
            //生成交易日志
            payLogDao.insertSelective(payLog);
            // 调用支付接口时还要获取数据，因此将支付日志数据放入缓存中
            redisTemplate.boundHashOps("paylog").put(name,payLog);

        }
        // 删除redis中购物车
        redisTemplate.boundHashOps("buyerCart").delete(name);
    }

    /**
     * 查询当前用户所有订单
     * @param userName
     */
    @Override
    public List<Order> findAll(String userName) {
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria orderListByUid = orderQuery.createCriteria().andUserIdEqualTo(userName);
        List<Order> orderList = orderDao.selectByExample(orderQuery);
        return orderList;

    }


}

