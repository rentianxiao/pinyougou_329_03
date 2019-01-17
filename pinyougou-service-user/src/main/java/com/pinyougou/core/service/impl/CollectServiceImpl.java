package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.CollectService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectServiceImpl implements CollectService {


    @Resource
    private ItemDao itemDao;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 收藏的分页查询
     * @param userName
     * @return
     */
    @Override
    public List<Item> search(String userName) {

        List<Item> itemList = new ArrayList<>();

        List<Long> itemIdList = (List) redisTemplate.boundHashOps(userName).get("collection");
        if (itemIdList != null && itemIdList.size() > 0) {
            for (Long itemId : itemIdList) {
                Item item1 = itemDao.selectByPrimaryKey(itemId);
                itemList.add(item1);
            }
        }
        return itemList;
    }
}
