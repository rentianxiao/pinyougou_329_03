package com.pinyougou.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.Item;

import java.util.List;

public interface CollectService {
    /**
     * 收藏的查询
     * @param userName
     * @return
     */
    public List<Item> search(String userName);
}
