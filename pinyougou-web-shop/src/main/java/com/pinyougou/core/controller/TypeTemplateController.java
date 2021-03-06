package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.TypeTemplateService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    /*回显模板*/
    @RequestMapping("findAll.do")
    public List<TypeTemplate> findAll(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return typeTemplateService.findAll(name);


    }


    /*添加*/
    @RequestMapping("add.do")
    public Result add(@RequestBody TypeTemplate typeTemplate){

        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            typeTemplateService.insert(typeTemplate,name);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }



    /**/
    @RequestMapping("updateStatus.do")
    public Result updateStatus(Long[]ids,String status){

        try {
            typeTemplateService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

}
