package com.pinyougou.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.vo.SpecificationVo;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    /**
     *  规格列表查询
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    public PageResult search(Integer page, Integer rows, Specification specification);

    /**
     * 规格保存
     * @param specificationVo
     */
    public void add(SpecificationVo specificationVo);

    /**
     * 规格回显
     * @param id
     * @return
     */
    public SpecificationVo findById(Long id);

    /**
     * 规格更新
     * @param specificationVo
     */
    public void update(SpecificationVo specificationVo);

    /**
     * 删除规格
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 新增模板时初始化规格列表
     * @return
     */
    public List<Map<String,String>> selectOptionList();
}
