package com.pinyougou.core.controller.collect;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.AddressService;
import com.pinyougou.core.service.CollectService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/collect")
public class collectController {

    @Reference
    private CollectService collectService;

    /**
     * 获取当前用户的收藏列表
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Item> findAll() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return collectService.search(userName);
    }
}
