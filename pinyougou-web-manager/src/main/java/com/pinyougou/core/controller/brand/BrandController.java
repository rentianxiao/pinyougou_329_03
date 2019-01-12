package com.pinyougou.core.controller.brand;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    /**
     * 品牌的分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/findByPage.do")
    public PageResult findPage(Integer pageNum, Integer pageSize){
        return brandService.findPage(pageNum,pageSize);
    }

    /**
     * 品牌的条件查询
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer pageNum, Integer pageSize,@RequestBody Brand brand){
        return brandService.search(pageNum,pageSize,brand);
    }

    /**
     * 新增品牌
     * @param brand
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody Brand brand){
        try {
            brandService.add(brand);
            return  new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"保存失败");
        }
    }

    /**
     * 品牌回显(根据id获取实体)
     * @param id
     * @return
     */
    @RequestMapping("/findById.do")
    public Brand findById(Long id){
        return  brandService.findOne(id);
    }

    /**
     * 修改品牌
     * @param brand
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return  new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"修改失败");
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return  new Result(true,"删除成功");

        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"删除失败");
        }
    }


    /**
     * 新增模板时初始化品牌列表
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map<String,String>> selectOptionList(){
        return brandService.selectOptionList();
    }

}
