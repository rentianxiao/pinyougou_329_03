package com.pinyougou.core.service;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有品牌
     * @return
     */
    public List<Brand> findAll();

    /**
     * 品牌的分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(Integer pageNum,Integer pageSize);

    /**
     * 品牌的条件查询
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    public PageResult search(Integer pageNum,Integer pageSize,Brand brand);

    /**
     * 新增品牌
     * @param brand
     */
    public void add(Brand brand);

    /**
     * 品牌回显(根据id获取实体)
     */
    public Brand findOne(Long id);

    /**
     * 修改品牌
     * @param brand
     */
    public void update(Brand brand);

    /**
     * 品牌的批量删除
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 新增模板时初始化品牌列表
     * @return
     */
    public List<Map<String,String>> selectOptionList();

    /**
     * 更新品牌状态
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);
}
