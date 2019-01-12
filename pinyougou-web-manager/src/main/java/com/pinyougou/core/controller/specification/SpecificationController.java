package com.pinyougou.core.controller.specification;


import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.vo.SpecificationVo;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.SpecificationService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /*
     * 规格列表查询
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification){
        return  specificationService.search(page, rows, specification);
    }

    /**
     * 保存规格
     * @param specificationVo
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody SpecificationVo specificationVo){
        try {
            specificationService.add(specificationVo);
            return  new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(true,"保存失败");
        }
    }

    /**
     * 规格回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public SpecificationVo findById(Long id){
        SpecificationVo specificationVo = specificationService.findById(id);
        return specificationVo;
    }

    /**
     * 规格更新
     * @param specificationVo
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody SpecificationVo specificationVo){
        try {
            specificationService.update(specificationVo);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    /**
     * 删除规格
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result detele(Long[] ids){
        try {
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /**
     * 新增模板时初始化规格列表
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map<String,String>> selectOptionList(){
        return specificationService.selectOptionList();
    }
}
