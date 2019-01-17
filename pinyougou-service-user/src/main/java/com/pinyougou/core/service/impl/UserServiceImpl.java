package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.opensaml.saml1.binding.artifact.AbstractSAML1Artifact;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import util.MD5Util;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination smsDestination;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserDao userDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private ItemDao itemDao;

    /**
     * 发送短信验证码
     *
     * @param phone
     */
    @Override
    public void sendCode(final String phone) {
        //封装手机号、短信签名、短信模板、模板参数，发送到MQ中
        //生成随机的6位数的验证码，模板参数需要的数据
        final String code = RandomStringUtils.randomNumeric(6);

        System.out.println("code = " + code);
        //保存验证码到redis中(服务器端session共享)
        redisTemplate.boundValueOps(phone).set(code);
        //设置过期时间
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNumbers", phone);
                mapMessage.setString("signName", "阮文");
                mapMessage.setString("templateCode", "SMS_140720901");
                mapMessage.setString("templateParam", "{\"code\":\"" + code + "\"}");

                return mapMessage;
            }
        });
    }

    /**
     * 用户注册
     *
     * @param smsCode
     * @param user
     */
    @Override
    public void add(String smsCode, User user) {
        //判断验证码是否正确
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (code != null && !"".equals(code) && code.equals(smsCode)) {
            // 保存用户
            //                                             原始密码，编码格式
            String password = MD5Util.MD5Encode(user.getPassword(), null);
            user.setPassword(password);
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        } else {
            throw new RuntimeException("验证码不正确");
        }
    }

    /**
     * 2019-1-14订单分页查询
     *
     * @param userName
     * @param page
     * @param rows
     * @param order
     * @return
     */
    @Override
    public PageResult search(String userName, Integer page, Integer rows, Order order) {
        //设置分页条件
        PageHelper.startPage(page, rows);

        //查询所有订单
        OrderQuery orderQuery = new OrderQuery();
        if (order.getStatus() == null ){
            orderQuery.createCriteria().andUserIdEqualTo(userName);
        }else if ("1".equals(order.getStatus()) || "7".equals(order.getStatus())||"4".equals(order.getStatus())){
            orderQuery.createCriteria().andUserIdEqualTo(userName).andStatusEqualTo(order.getStatus());
        }else if ("3".equals(order.getStatus())){
            orderQuery.createCriteria().andUserIdEqualTo(userName).andStatusEqualTo(order.getStatus()).andStatusEqualTo("2");
        }

        Page<Order> orderList = (Page<Order>) orderDao.selectByExample(orderQuery);
        //用来等着其他的字段

        if (orderList != null && orderList.size()>0){
            for (Order order1 : orderList) {
                OrderItemQuery orderItemQuery = new OrderItemQuery();
                orderItemQuery.createCriteria().andOrderIdEqualTo(order1.getOrderId());
                List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
                if (orderItemList != null && orderList.size()>0){
                    for (OrderItem orderItem : orderItemList) {
                        Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                        orderItem.setSpellMap(item.getSpecMap());
                        orderItem.setCostPirce(item.getCostPirce());
                        orderItem.setMarketPrice(item.getMarketPrice());
                    }
                }
                Seller seller = sellerDao.selectByPrimaryKey(order1.getSellerId());
                String nickName = seller.getNickName();
                order1.setNick_name(nickName);
                order1.setOrderItemList(orderItemList);
            }
        }

        //将结果封装到PageResult中并返回
        PageResult pageResult = new PageResult(orderList.getResult(), orderList.getTotal());
        return pageResult;
    }

    /**
     * 用户信息查询
     * @param userName
     * @return
     */
    @Override
    public List<User> findUser(String userName) {
        UserQuery userQuery = new UserQuery();
        userQuery.createCriteria().andUsernameEqualTo(userName);
        List<User> userList = userDao.selectByExample(userQuery);
        return userList;
    }
}
