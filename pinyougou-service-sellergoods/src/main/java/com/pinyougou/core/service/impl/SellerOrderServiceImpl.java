package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.order.*;
import cn.itcast.core.pojo.vo.SellerOrderVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.SellerOrderService;
import util.DateUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SellerOrderServiceImpl implements SellerOrderService {

    ////商品名称(orderItem)
    ////商品价格(orderItem)
    ////商品数量(orderItem)
    ////订单实付金额(order)
    ////订单来源(order)
    ////创建时间(order)
    ////状态(order)

    @Resource
    private OrderDao orderDao;

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private OrderItemDao orderItemDao;

    @Override
    public List<SellerOrder> findOrderBySellerId(String sellerId) {
        List<SellerOrder> sellerOrderList = new ArrayList<>();
        OrderItemQuery orderItemQuery = new OrderItemQuery();
//        List<OrderItem> orderItemList= (List<OrderItem>) orderItemQuery.createCriteria().andSellerIdEqualTo(sellerId);
        orderItemQuery.createCriteria().andSellerIdEqualTo(sellerId);
        List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
        for (OrderItem orderItem : orderItemList) {
            SellerOrder sellerOrder = new SellerOrder();
            sellerOrder.setItemId(orderItem.getItemId());//商品id
            sellerOrder.setTitle(orderItem.getTitle());////商品名称(orderItem)
            sellerOrder.setPrice(orderItem.getPrice());////商品价格(orderItem)
            sellerOrder.setNum(orderItem.getNum());////商品数量(orderItem)
            Order order = orderDao.selectByPrimaryKey(orderItem.getOrderId());
            sellerOrder.setPayment(order.getPayment());////订单实付金额(order)
            sellerOrder.setSourceType(order.getSourceType());////订单来源(order)
            sellerOrder.setCreateTime(order.getCreateTime());////创建时间(order)
            sellerOrder.setStatus(order.getStatus());////状态(order)
            sellerOrderList.add(sellerOrder);
        }
        return sellerOrderList;
    }

    //订单发货
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            OrderItemQuery orderItemQuery = new OrderItemQuery();
            for (Long itemId : ids) {
                orderItemQuery.createCriteria().andItemIdEqualTo(itemId);
                List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
                for (OrderItem orderItem : orderItemList) {
                    Order order = orderDao.selectByPrimaryKey(orderItem.getOrderId());
                    if (order.getStatus().equals("2")) {
                        order.setStatus(status);
                        orderDao.updateByPrimaryKeySelective(order);
                    } else {
                        throw new RuntimeException("失败");
                    }

                }
            }
        }
    }

    @Override
    public List<SellerOrderVo> selectByStatusAndData(String sellerId, String status, String dateLimit) throws ParseException {
        List<SellerOrderVo> sellerOrderVoList = new ArrayList<>();//新建
        Double totalPrice = 0.0;

        OrderItemQuery orderItemQuery = new OrderItemQuery();
//        List<OrderItem> orderItemList = new ArrayList<>();
        OrderQuery orderQuery = new OrderQuery();
//        sellerOrderVo.setSellerOrderList();
        //查询状态为status的订单
        GoodsQuery goodsQuery = new GoodsQuery();
        //sellerOrderVoList=null;
        if (status == null && dateLimit == null) {
            orderQuery.createCriteria().andSellerIdEqualTo(sellerId);
            List<Order> orderList = orderDao.selectByExample(orderQuery);//商家id下所有订单
            //订单id(order),商品id(orderItem),SPU名(goods),实付金额
            for (Order order : orderList) {


                orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());//根据订单ID查询orderItem
                List<OrderItem> orderItemList1 = orderItemDao.selectByExample(orderItemQuery);
                for (OrderItem orderItem : orderItemList1) {

                    Goods goods = goodsDao.selectByPrimaryKey(orderItem.getGoodsId());
                    SellerOrderVo sellerOrderVo = new SellerOrderVo();
                    sellerOrderVo.setGoodsName(goods.getGoodsName());
                    sellerOrderVo.setItemId(orderItem.getItemId());
                    sellerOrderVo.setOrderId(order.getOrderId());
                    totalPrice+=goods.getPrice().doubleValue()*orderItem.getNum().doubleValue();
                    sellerOrderVo.setTotalPrice(new BigDecimal(totalPrice));
                    sellerOrderVoList.add(sellerOrderVo);
                }
            }


        } else {

            orderQuery.createCriteria().andStatusEqualTo(status).andSellerIdEqualTo(sellerId);
//            List<Order> orderList = orderDao.selectByExample(orderQuery);
//            for (Order order : orderList) {
                if (dateLimit .equals("1")) {

                    //查询当天订单,销售总额,订单id,订单来源,物流单号

                    String[] dayStartAndEndTimePointStr = DateUtils.getDayStartAndEndTimePointStr(new Date());
                    String startTime = dayStartAndEndTimePointStr[0];
                    Date sDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startTime);
                    String endTime = dayStartAndEndTimePointStr[1];
                    Date eDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endTime);
                    orderQuery.createCriteria().andEndTimeBetween(sDate, eDate);
                    List<Order> orderList1 = orderDao.selectByExample(orderQuery);
                    for (Order order1 : orderList1) {
                        orderItemQuery.createCriteria().andOrderIdEqualTo(order1.getOrderId());
                        List<OrderItem> orderItemList1 = orderItemDao.selectByExample(orderItemQuery);
                        for (OrderItem orderItem : orderItemList1) {
                            Goods goods = goodsDao.selectByPrimaryKey(orderItem.getGoodsId());
                            SellerOrderVo sellerOrderVo = new SellerOrderVo();
                            sellerOrderVo.setGoodsName(goods.getGoodsName());//设置商品名称
                            sellerOrderVo.setItemId(orderItem.getItemId());//设置商品id
                            totalPrice+=orderItem.getPrice().doubleValue()*orderItem.getNum().doubleValue();
                            sellerOrderVo.setTotalPrice(new BigDecimal(totalPrice));
                            sellerOrderVo.setOrderId(order1.getOrderId());//设置订单id
                            sellerOrderVoList.add(sellerOrderVo);//添加sellerOrderVo对象
                        }
                    }
                } else if (dateLimit .equals("2")) {
                    //查询本周订单,销售总额,订单id,订单来源,物流单号

                    Date[] weekStartAndEndDate = DateUtils.getWeekStartAndEndDate(new Date());

                    Date startTime = weekStartAndEndDate[0];
                    Date endTime = weekStartAndEndDate[1];

                    orderQuery.createCriteria().andEndTimeBetween(startTime, endTime);
                    List<Order> orderList1 = orderDao.selectByExample(orderQuery);
                    for (Order order1 : orderList1) {
                        orderItemQuery.createCriteria().andOrderIdEqualTo(order1.getOrderId());
                        List<OrderItem> orderItemList1 = orderItemDao.selectByExample(orderItemQuery);
                        for (OrderItem orderItem : orderItemList1) {
                            Goods goods = goodsDao.selectByPrimaryKey(orderItem.getGoodsId());
                            SellerOrderVo sellerOrderVo = new SellerOrderVo();
                            sellerOrderVo.setGoodsName(goods.getGoodsName());//设置商品名称
                            sellerOrderVo.setItemId(orderItem.getItemId());//设置商品id
                            totalPrice+=orderItem.getPrice().doubleValue()*orderItem.getNum().doubleValue();
                            sellerOrderVo.setTotalPrice(new BigDecimal(totalPrice));
                            sellerOrderVo.setOrderId(order1.getOrderId());//设置订单id
                            sellerOrderVoList.add(sellerOrderVo);//添加sellerOrderVo对象
                        }
                    }
                } else if (dateLimit .equals("3")) {
                    //查询本月订单,销售总额,订单id,订单来源,物流单号

                    Date[] monthStartAndEndDate = DateUtils.getMonthStartAndEndDate(new Date());

                    Date startTime = monthStartAndEndDate[0];
                    Date endTime = monthStartAndEndDate[1];

                    orderQuery.createCriteria().andEndTimeBetween(startTime, endTime);
                    List<Order> orderList1 = orderDao.selectByExample(orderQuery);
                    for (Order order1 : orderList1) {
                        orderItemQuery.createCriteria().andOrderIdEqualTo(order1.getOrderId());
                        List<OrderItem> orderItemList1 = orderItemDao.selectByExample(orderItemQuery);
                        for (OrderItem orderItem : orderItemList1) {
                            Goods goods = goodsDao.selectByPrimaryKey(orderItem.getGoodsId());
                            System.out.println(goods.getSellerId());
                            SellerOrderVo sellerOrderVo = new SellerOrderVo();
                            sellerOrderVo.setGoodsName(goods.getGoodsName());//设置商品名称
                            sellerOrderVo.setItemId(orderItem.getItemId());//设置商品id
                            totalPrice+=orderItem.getPrice().doubleValue()*orderItem.getNum().doubleValue();
                            sellerOrderVo.setTotalPrice(new BigDecimal(totalPrice));
                            sellerOrderVo.setOrderId(order1.getOrderId());//设置订单id
                            sellerOrderVoList.add(sellerOrderVo);//添加sellerOrderVo对象
                        }
                    }
                }

//            }
        }
        return sellerOrderVoList;
    }
}
