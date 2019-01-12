package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import util.MD5Util;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination smsDestination;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserDao userDao;

    /**
     * 发送短信验证码
     * @param phone
     */
    @Override
    public void sendCode(final String phone) {
        //封装手机号、短信签名、短信模板、模板参数，发送到MQ中
        //生成随机的6位数的验证码，模板参数需要的数据
        final String code = RandomStringUtils.randomNumeric(6);

        System.out.println("code = " +code);
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
                mapMessage.setString("templateParam", "{\"code\":\""+code+"\"}");

                return mapMessage;
            }
        });
    }

    /**
     * 用户注册
     * @param smsCode
     * @param user
     */
    @Override
    public void add(String smsCode,User user) {
        //判断验证码是否正确
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (code != null && !"".equals(code) && code.equals(smsCode)){
            // 保存用户
            //                                             原始密码，编码格式
            String password = MD5Util.MD5Encode(user.getPassword(), null);
            user.setPassword(password);
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        }else{
            throw new RuntimeException("验证码不正确");
        }
    }
}
