package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.TypeTemplateService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 模板列表查询的同时将数据写入到缓存中
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //查询所有模板数据放入到缓存中
        List<TypeTemplate> typeTemplatelist = typeTemplateDao.selectByExample(null);
        if (typeTemplatelist != null && typeTemplatelist.size() > 0) {
            for (TypeTemplate template : typeTemplatelist) {
                //缓存该模板下的品牌
                List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                //缓存该模板下的规格
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            }
        }
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (typeTemplate.getName() != null && !"".equals(typeTemplate.getName().trim())){
            typeTemplateQuery.createCriteria().andNameLike("%"+typeTemplate.getName().trim()+"%");
        }
        //设置查询结果升序或降序
        typeTemplateQuery.setOrderByClause("id desc");
        //查询
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(p.getResult(),p.getTotal());
    }

    /**
     * 新增模板
     * @param typeTemplate
     * @return
     */
    @Transactional
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 根据模板ID加载出对应的品牌以及扩展属性
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 通过模板ID加载出对应的规格规格以及规格选项
     * @param id
     * @return
     */
    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //取出规格
        String specIds = typeTemplate.getSpecIds(); //例：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        //将json字符串转成对象
        List<Map> specList = JSON.parseArray(specIds, Map.class);
        //通过规格获取对应的规格选项
        if (specList != null && specList.size() > 0) {
            for (Map map : specList) {
                String specId = map.get("id").toString();
                //获取对应的规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                query.createCriteria().andSpecIdEqualTo(Long.valueOf(specId));
                List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
                map.put("options",specificationOptionList);
            }
        }
        //specList:[{"id":27,"text":"网络","options":[{},{}...]}]
        return specList;
    }


    /*回显模板
     * -- 张斌
     * */
    @Override
    public List<TypeTemplate> findAll(String name) {


        return typeTemplateDao.select(name);
    }



    /*添加
     * -- 张斌
     * */
    @Transactional
    @Override
    public void insert(TypeTemplate typeTemplate,String name) {
        typeTemplate.setSellerId(name);
        typeTemplate.setStatus("0");
        typeTemplateDao.insertSelective(typeTemplate);
    }
    /*提交申请
     * -- 张斌
     * */
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            TypeTemplate specification = new TypeTemplate();
            specification.setStatus(status);
            for (Long id : ids) {
                specification.setId(id);
                typeTemplateDao.updateByPrimaryKeySelective(specification);

            }
        }
    }
}
