package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.ItemCatService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 商品分类列表查询的同时将数据写到缓存中
     * @param parentId
     * @return
     */
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        // 查询所有分类放入缓存：分类名称---模板id
        List<ItemCat> itemCats= itemCatDao.selectByExample(null);
        if (itemCats != null && itemCats.size() > 0) {
            for (ItemCat itemCat : itemCats) {
                //分类保存到缓存中 key：最后用分类主键id   但是页面传递的是name
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
        }
        // 设置查询条件
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);

        List<ItemCat> itemCatList = itemCatDao.selectByExample(itemCatQuery);

        //放入到缓存中
        return itemCatList;
    }

    /**
     * 根据三级分类加载模板ID
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    /**
     * 商品的列表回显具体的分类名称
     * @return
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}
