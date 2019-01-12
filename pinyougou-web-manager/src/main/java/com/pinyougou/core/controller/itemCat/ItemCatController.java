package com.pinyougou.core.controller.itemCat;

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
     * 商品分类列表查询
     * @param parentId
     * @return
     */
    @RequestMapping("/findByParentId.do")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    /**
     * 显示分类名称
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}
