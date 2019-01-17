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


    /*
    * 根据用户名查出全部  --张斌
    * */
    List<ItemCat> findByName(String name);
    /*
    * 根据用户名和 id 查出全部  --张斌
    *
    * */
    List<ItemCat> findByParentId(Long parentId,String name);


    /*
    * 修改状态 --张斌
    * */
    void updateStatus(Long[] ids, String status);

    /*
    * 保存 --张斌
    * */
    void save(ItemCat itemCat,String name);
}
