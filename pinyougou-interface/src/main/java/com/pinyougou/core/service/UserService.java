package com.pinyougou.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;

import java.util.List;

public interface UserService {

    /**
     * 发送短信验证码
     * @param phone
     */
    public void sendCode(String phone);

    /**
     * 用户注册
     * @param smsCode
     * @param user
     */
    public void add(String smsCode,User user);

    /**
     * 2019-1-14订单的分页查询
     * @param userName
     * @param page
     * @param rows
     * @param order
     * @return
     */
    public PageResult search(String userName, Integer page, Integer rows, Order order);

    /**
     * 用户信息查询
     * @param userName
     * @return
     */
    List<User> findUser(String userName);
}
