package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.pojo.vo.SpecificationVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.SpecificationService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements SpecificationService {


    @Resource
    private SpecificationDao specificationDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 规格列表查询
     * @param rows
     * @param specification
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        // 设置分页条件
        PageHelper.startPage(page, rows);
        // 设置查询条件
        SpecificationQuery specificationQuery = new SpecificationQuery();
        if (specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())) {
            specificationQuery.createCriteria().andSpecNameLike("%" + specification.getSpecName().trim() + "%");
        }
        specificationQuery.setOrderByClause("id desc");
        //根据条件查询
        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        //将结果封装并返回
        return new PageResult(p.getResult(), p.getTotal());
    }

    /**
     * 规格保存
     * @param specificationVo
     */
    @Transactional
    @Override
    public void add(SpecificationVo specificationVo) {
        // 保存规格
        Specification specification = specificationVo.getSpecification();
        specificationDao.insertSelective(specification); // 返回自增主键id
        // 保存规格选项
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        if (specificationOptionList != null && specificationOptionList.size() > 0) {
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId()); // 设置外键
            }
            // 批量插入
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    /**
     * 规格回显
     * @param id
     * @return
     */
    @Override
    public SpecificationVo findById(Long id) {
        SpecificationVo specificationVo = new SpecificationVo();
        //查询规格
        Specification specification = specificationDao.selectByPrimaryKey(id);
        specificationVo.setSpecification(specification);
        //查询规格选项
        SpecificationOptionQuery specificationOptionQuery
                = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(specificationOptionQuery);
        specificationVo.setSpecificationOptionList(specificationOptionList);

        return specificationVo;
    }

    /**
     * 规格更新
     * @param specificationVo
     */
    @Transactional
    public void update(SpecificationVo specificationVo) {
        //更新规格
        Specification specification = specificationVo.getSpecification();
        specificationDao.updateByPrimaryKeySelective(specification);

        //更新规格选项:先删除再插入
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(specification.getId());
        //先删除
        specificationOptionDao.deleteByExample(specificationOptionQuery);
        //再插入
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        if (specificationOptionList != null && specificationOptionList.size() > 0) {
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId()); // 设置外键
            }
            // 批量插入
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    /**
     * 删除规格
     * @param ids
     */
    @Transactional
    public void delete(Long[] ids) {
        if (ids != null && ids.length>0){
            for (Long id : ids) {
                //删除规格选项
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
                specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
                specificationOptionDao.deleteByExample(specificationOptionQuery);
            }
            //删除规格
            specificationDao.deleteByPrimaryKeys(ids);
        }
    }

    /**
     * 新增模板时初始化规格列表
     * @return
     */
    @Override
    public List<Map<String, String>> selectOptionList() {
        return specificationDao.selectOptionList();
    }





    /*
     * 通过用户名查询全部 --张斌
     * */
    @Override
    public List<Specification> findAllByUser(String name) {

        return specificationDao.selectByName(name);
    }
    /*添加品牌 --张斌*/
    @Transactional
    @Override
    public void addList(SpecificationVo specVo, String name) {
        Specification specification = specVo.getSpecification();
        specification.setSellerId(name);
        specification.setStatus("0");
        specificationDao.insert(specification);
        List<SpecificationOption> specificationOptionList = specVo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            specificationOption.setId(specification.getId());
            specificationOptionDao.insert(specificationOption);
        }



    }
    /*
     * 修改审核状态 --张斌
     * */
    @Override
    public void update(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            Specification specification = new Specification();
            specification.setStatus(status);
            for (Long id : ids) {
                specification.setId(id);
                specificationDao.updateByPrimaryKeySelective(specification);

            }
        }


    }
    /*
     * 新增模板时根据状态
     * 初始化品牌列表    --张斌
     *
     * */
    @Override
    public List<Map<String, String>> selectOption() {
        String status="2";
        return specificationDao.selectOption(status);
    }






}
