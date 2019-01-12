package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.AddressService;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Resource
    private AddressDao addressDao;

    /**
     * 获取当前登录人的收货地址列表
     * @param name
     * @return
     */
    @Override
    public List<Address> findListByLoginUser(String name) {
        AddressQuery addressQuery = new AddressQuery();
        addressQuery.createCriteria().andUserIdEqualTo(name);
        List<Address> addressList = addressDao.selectByExample(addressQuery);
        return addressList;
    }
}
