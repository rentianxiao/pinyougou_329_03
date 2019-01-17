package com.pinyougou.core.controller;


import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.BrandService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {

 @Reference
    private BrandService brandService;


    /*根据名字查询全部  --张斌*/
    @RequestMapping("findAll.do")
    public List<Brand> findAll(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return  brandService.findAllByUser(name);

    }

    /*
     *
     * 添加品牌 --张斌
     * */
    @RequestMapping("save.do")
    public Result add(@RequestBody Brand brand){

        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            brandService.add(brand,name);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    /*
    修改状态 --张斌
    * */
    @RequestMapping("updateStatus.do")
    public Result updateStatus(Long[] ids, String status){
        try {
            brandService.updateStatus(ids,status);
            return new Result(true,"提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交失败");
        }

    }

    /*新增模板时根据状态--张斌
     * 初始化品牌列表*/
     @RequestMapping("selectOptionList.do")
    public List<Map<String,String>> selectOptionList(){

        return brandService.selectOptionList();
     }




}
