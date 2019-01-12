package cn.itcast;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-dao.xml"})
public class MybatisTest {
    @Autowired
    private BrandDao brandDao;

    @Test
    public  void findAll(){
        List<Brand> brandList = brandDao.selectByExample(null);
        System.out.println(brandList);
    }

    @Test
    public  void findById(){
        Brand brand = brandDao.selectByPrimaryKey(1L);
        System.out.println(brand);
    }

    @Test
    public  void findBrandByExample(){
        BrandQuery brandQuery = new BrandQuery();
        brandQuery.setFields("id,name");
        brandQuery.setOrderByClause("id");
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        criteria.andNameLike("%海%");
        List<Brand> brandList = brandDao.selectByExample(brandQuery);
        System.out.println(brandList);
    }
}
