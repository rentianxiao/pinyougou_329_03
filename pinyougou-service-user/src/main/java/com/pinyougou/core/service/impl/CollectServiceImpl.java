package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.CollectService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

        Map<String, Map<String, List<Item>>> userMap = (Map<String, Map<String, List<Item>>>) redisTemplate.boundHashOps(userName).get("collection");
        List<Item> collectionList = userMap.get(userName).get("collection");

        return collectionList;
    }
}
