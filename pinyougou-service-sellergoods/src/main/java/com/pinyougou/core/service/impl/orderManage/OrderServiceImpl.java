package com.pinyougou.core.service.impl.orderManage;

import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.orderManage.OrderService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;

    /**
     * 订单列表查询
     * @param page
     * @param rows
     * @param order
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Order order) {
        PageHelper.startPage(page, rows);

        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        if (order.getUserId() != null && !"".equals(order.getUserId())) {
            criteria.andUserIdEqualTo(order.getUserId().trim());
        }
        Page<Order> p = (Page<Order>) orderDao.selectByExample(orderQuery);

        return new PageResult(p.getResult(), p.getTotal());
    }

    /**
     * 查询所有订单
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderDao.selectByExample(null);
    }

    /**
     * 保存订单
     * @param order
     */
    @Override
    public void saveOrder(Order order) {
        orderDao.insertSelective(order);
    }
}
