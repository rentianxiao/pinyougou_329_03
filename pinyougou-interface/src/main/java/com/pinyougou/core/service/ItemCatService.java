package com.pinyougou.core.service;


import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    /**
     * 商品分类列表查询
     * @param parentId
     * @return
     */
    public List<ItemCat> findByParentId(Long parentId);

    /**
     * 根据三级分类加载模板ID
     * @param id
     * @return
     */
    public ItemCat findOne(Long id);

    /**
     * 商品的列表回显具体的分类名称
     * @return
     */
    public List<ItemCat> findAll();
}
