package com.pinyougou.core.controller.address;

import cn.itcast.core.pojo.address.Address;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    /**
     * 获取当前登录人的收货地址列表
     * @return
     */
    @RequestMapping("/findListByLoginUser.do")
    public List<Address> findListByLoginUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findListByLoginUser(userName);
    }
}
