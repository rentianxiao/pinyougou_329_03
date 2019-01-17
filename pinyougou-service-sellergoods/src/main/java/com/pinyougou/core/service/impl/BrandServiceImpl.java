package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    /**
     * Resource跟Autowired的区别
     * 1.Autowired 根据类型进行匹配,Resourcer根据名称进行匹配
     * 2.Resource由JDK提供的,Autowired由spring提供
     * <p>
     * Resource和Autowired相比,使用Resource有哪些好处
     * 1.Autowired由spring提供,而spring提供的服务越多,框架性能越低
     * 2.Resource由JDK提供的,可以降低与框架间的耦合度
     */
    @Resource
    private BrandDao brandDao;

    /**
     * 查询所有品牌
     *
     * @return
     */
    @Override
    public List<Brand> findAll() {
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }

    /**
     * 品牌的分页查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //设置分页条件
        PageHelper.startPage(pageNum, pageSize);
        //根据条件查询结果集
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
        //将结果封装到PageResult中并返回
        PageResult pageResult = new PageResult(page.getResult(), page.getTotal());
        return pageResult;
    }

    /**
     * 品牌的条件查询
     *
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {
        //1.设置分页条件
        PageHelper.startPage(pageNum, pageSize);
        //2.设置查询条件
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        if (brand.getName() != null && !"".equals(brand.getName().trim())) {
            criteria.andNameLike("%" + brand.getName().trim() + "%");
        }
        if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar().trim())) {
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }
        //设置根据id降序
        brandQuery.setOrderByClause("id desc");
        //3.根据条件查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);

        //4.将结果封装到PageResult中并返回
        PageResult pageResult = new PageResult(page.getResult(), page.getTotal());
        return pageResult;
    }

    /**
     * 新增品牌
     *
     * @param brand
     */
    @Transactional
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    /**
     * 品牌回显(根据id获取实体)
     *
     * @param id
     * @return
     */
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * 修改品牌
     *
     * @param brand
     */
    @Transactional
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    /**
     * 品牌的批量删除
     * @param ids
     */
    @Transactional
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            /*
            for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }
            */
            //一条条删除,频繁获取释放连接,效率低
            brandDao.deleteByPrimaryKeys(ids);

        }
    }

    /**
     * 新增模板时初始化品牌列表
     * @return
     */
    @Override
    public List<Map<String, String>> selectOptionList() {
        return brandDao.selectOptionList();
    }




    /*
    * 通过用户名查询全部 --张斌
    * */
    @Override
    public List<Brand> findAllByUser(String name) {

        return brandDao.selectByName(name);

    }

    /*添加品牌 --张斌*/
    @Transactional
    @Override
    public void add(Brand brand,String name) {
        brand.setBrandStatus("0");
        brand.setSellerId(name);
        brandDao.insertSelective(brand);
    }

    /*
    * 修改审核状态 --张斌
    * */
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            Brand brand = new Brand();
            brand.setBrandStatus(status);
            for (Long id : ids) {
                brand.setId(id);
                brandDao.updateByPrimaryKeySelective(brand);

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
        return brandDao.selectOption(status);
    }

    @Override
    public PageResult searchAll(Integer pageNum, Integer pageSize, Brand brand, String name) {
        //1.设置分页条件
        PageHelper.startPage(pageNum, pageSize);
        //2.设置查询条件
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        if (brand.getName() != null && !"".equals(brand.getName().trim())) {
            criteria.andNameLike("%" + brand.getName().trim() + "%");
        }
        if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar().trim())) {
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }
        //设置根据id降序
        brandQuery.setOrderByClause("id desc");
        //3.根据条件查询
        Page<Brand> page = (Page<Brand>) brandDao.searchAll(brandQuery,name);

        //4.将结果封装到PageResult中并返回
        PageResult pageResult = new PageResult(page.getResult(), page.getTotal());
        return pageResult;


    }


}
