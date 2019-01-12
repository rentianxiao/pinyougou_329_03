package com.pinyougou.core.task;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class RedisTimeTask {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    //将商品分类同步到缓存
    //时间表达式  秒 分 时 日 月 年
    @Scheduled(cron = "00 39 17 27 12 ?")
    public void autoToRedisForItemCat(){
        // 查询所有分类放入缓存：分类名称---模板id
        List<ItemCat> itemCats= itemCatDao.selectByExample(null);
        if (itemCats != null && itemCats.size() > 0) {
            for (ItemCat itemCat : itemCats) {
                //分类保存到缓存中 key：最后用分类主键id   但是页面传递的是name
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
        }
        System.out.println("定时器执行了");
    }

    //将商品模板同步到缓存
    @Scheduled(cron = "00 39 17 27 12 ?")
    public void autoToRedisForTemplate(){
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
        System.out.println("定时器执行了");
    }

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
}
