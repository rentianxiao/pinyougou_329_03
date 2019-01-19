package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.item.ItemCat;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.ItemCatService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /**
     * 商品列表回显具体的分类名称
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<ItemCat> findAll(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return itemCatService.findByName(name);
    }

    /*商品分类列表查询*/
    @RequestMapping("/findByParentId.do")
    public List<ItemCat> findByParentId(Long parentId){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return itemCatService.findByParentId(parentId,name);
    }


    @RequestMapping("updateStatus.do")
    public Result updateStatus(Long[]ids, String status){

        try {
            itemCatService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }


    @RequestMapping("add.do")
    public Result save(@RequestBody ItemCat itemCat){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            itemCatService.save(itemCat,name);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }




    }
}
