package com.pinyougou.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /**
     * 商品录入加载分类列表
     * @param parentId
     * @return
     */
    @RequestMapping("/findByParentId.do")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    /**
     * 根据三级分类加载模板ID
     * @param id
     * @return
     */
    @RequestMapping("findOne.do")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    /**
     * 商品的列表回显具体的分类名称
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}
