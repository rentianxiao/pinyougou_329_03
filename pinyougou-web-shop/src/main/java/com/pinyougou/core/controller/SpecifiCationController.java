package com.pinyougou.core.controller;


import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.vo.SpecificationVo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.SpecificationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("specification")
public class SpecifiCationController {

    @Reference
    private SpecificationService specService;

    @RequestMapping("findAll.do")
    public List<Specification> findAll(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return  specService.findAllByUser(name);

    }

    @RequestMapping("add.do")
    public Result add(@RequestBody SpecificationVo specVo){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            specService.addList(specVo,name);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }

    }


    @RequestMapping("updateStatus.do")
    public Result updateStatus(Long[] ids, String status){

        try {
            specService.update(ids, status);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }


    }

    @RequestMapping("selectOptionList.do")
    public List<Map<String,String>> selectOptionList(){
        return specService.selectOptionList();
    }


}
