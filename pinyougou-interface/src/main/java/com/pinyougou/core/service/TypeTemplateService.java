package com.pinyougou.core.service;


import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

    /**
     * 查询模板列表
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    public PageResult search(Integer page , Integer rows , TypeTemplate typeTemplate);

    /**
     * 新增模板
     * @param typeTemplate
     * @return
     */
    void add(TypeTemplate typeTemplate);

    //回显模板
    //更新模板

    /**
     * 根据模板ID加载除对应的品牌以及扩展属性
     * @param id
     * @return
     */
    public TypeTemplate findOne(Long id);

    /**
     * 通过模板ID加载出对应的规格规格以及规格选项
     * @return
     */
    public List<Map> findBySpecList(Long id);

    /**
     * 更新模板状态
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);
}
