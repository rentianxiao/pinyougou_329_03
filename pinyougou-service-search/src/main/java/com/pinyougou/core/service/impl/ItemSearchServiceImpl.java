package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.core.service.ItemSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService{

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ItemDao itemDao;

    /**
     * 前台系统的检索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map<String,String> searchMap){
        //对关键字进行去中间空格处理
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replace(" ","");   //去除中间所有空格
            searchMap.put("keywords", keywords);
        }
        //创建封装结果集的map
        Map<String, Object> resultMap = new HashMap<>();
        //关键字检索且高亮显示并且分页
        Map<String,Object> map = searchHighlightPage(searchMap);
        resultMap.putAll(map);
        // 商品分类结果集：categoryList
        List<String> categoryList = searchForGroupPage(searchMap);
        if(categoryList != null && categoryList.size() > 0){
            //通过商品分类加载该分类下对应的品牌以及规格结果集（默认加载第一个分类下的品牌以及规格）
            Map<String,Object> brandAndSpecMap = searchBrandsAndSpecsByFirstCategory(categoryList.get(0));
            resultMap.putAll(brandAndSpecMap);
            resultMap.put("categoryList", categoryList);
        }
        return  resultMap;
    }

    /**
     * 将商品信息保存到索引库中
     * @param id
     */
    public void updateSolr(Long id) {
        // 根据商品id查询对应的库存信息
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andStatusEqualTo("1").
                andIsDefaultEqualTo("1").andGoodsIdEqualTo(id);

        List<Item> itemList = itemDao.selectByExample(itemQuery);
        //数据库中拿到的是json字符串：{"机身内存":"16G","网络":"联通3G"}
        //我们希望最终拼接的动态字段： {"item_spec_机身内存":"16G","item_spec_网络":"联通3G"}
        //所以需要我们处理规格
        if (itemList != null && itemList.size() > 0){
            for (Item item : itemList) {
                String spec = item.getSpec();
                //将json字符串转换成Map集合
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }
    }

    /**
     * 删除索引库中商品
     * @param id
     */
    public void deleteItemFromSolr(Long id) {
        SimpleQuery query = new SimpleQuery("item_goodsid:"+id);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //通过商品分类加载该分类下对应的品牌以及规格结果集(默认查询第一个分类下的)
    private Map<String,Object> searchBrandsAndSpecsByFirstCategory(String categoryName) {
        //创建Map,封装结果集
        Map<String, Object> brandAndSpecMap = new HashMap<>();
        //通过分类名称获取模板id
        Object typeId = redisTemplate.boundHashOps("itemCat").get(categoryName);

        //通过模板id获取品牌结果集
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        brandAndSpecMap.put("brandList",brandList);
        //通过模板id获取规格结果集
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        brandAndSpecMap.put("specList",specList);
        return brandAndSpecMap;
    }

    //查询商品分类
    private List<String> searchForGroupPage(Map<String, String> searchMap) {
        // 设置检索关键字
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)){
            criteria.is(keywords);
        }
        SimpleQuery query = new SimpleQuery(criteria);
        // 设置分组查询条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");  //根据哪个字段进行分组
        query.setGroupOptions(groupOptions);
        // 根据条件检索
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);
        // 获取分组结果
        List<String> list = new ArrayList<>();
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");//获取该字段分组下的结果
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();// 分组结果
            list.add(groupValue);
        }
        return list;
    }

    //关键字检索且高亮显示并且分页
    private Map<String,Object> searchHighlightPage(Map<String, String> searchMap) {
        //1.设置检索关键字
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)){
            criteria.is(keywords);
        }
        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);
        //2.设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer offSet = (pageNo - 1) * pageSize;
        query.setOffset(offSet);      //设置起始行
        query.setRows(pageSize);        //每页显示的条数
        //3.设置高亮显示条件
        HighlightOptions highlightOptions = new HighlightOptions(); //设置高亮
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        highlightOptions.addField("item_title");//如果该字段包含关键字需要高亮显示
        query.setHighlightOptions(highlightOptions);
        //设置过滤条件
            //根据商品分类过滤
        String category = searchMap.get("category");
        if (category != null && !"".equals(category)) {
            Criteria cri = new Criteria("item_category");
            cri.is(category);
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri );
            query.addFilterQuery(filterQuery);
        }
            //根据品牌过滤
        String brand = searchMap.get("brand");
        if (brand != null && !"".equals(brand)) {
            Criteria cri = new Criteria("item_brand");
            cri.is(brand);
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri );
            query.addFilterQuery(filterQuery);
        }
            //根据商品规格过滤
        String spec = searchMap.get("spec");
        if (spec != null && !"".equals(spec)) {
            Map<String,String> specMap = JSON.parseObject(spec,Map.class);
            Set<Map.Entry<String, String>> entrySet = specMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                Criteria cri = new Criteria("item_spec_" + entry.getKey());
                cri.is(entry.getValue());
                FilterQuery filterQuery = new SimpleFilterQuery(cri);
                query.addFilterQuery(filterQuery);
            }
        }
            //根据价格过滤
        String price = searchMap.get("price");
        if (price != null && !"".equals(price)) {
            //传递的价格;  区间段：min - max   xxx以上 ：min - *
            String[] prices = price.split("-");
            Criteria cri = new Criteria("item_price");
            if (price.contains("*")) {
                cri.greaterThan(prices[0]);
            }else {
                cri.between(prices[0],prices[1],true,true);
            }
            FilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);

        }

        // 根据价格以及新品排序：sortField：排序字段    sort：传递的值
        String sort = searchMap.get("sort");
        String sortField = searchMap.get("sortField");
        if (sort != null && !"".equals(sort)) {
            if ("ASC".equals(sort)){
                Sort s = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(s);
            }else {
                Sort s = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(s);
            }

        }

        //4、根据关键字检索并且结果分页
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);
            //将高亮的结果重新设置到普通的title上
        List<HighlightEntry<Item>> highlightEntryList = highlightPage.getHighlighted();
        if (highlightEntryList != null && highlightEntryList.size() > 0) {
            for (HighlightEntry<Item> itemHighlightEntry : highlightEntryList) {
                Item item = itemHighlightEntry.getEntity(); //普通结果集
                List<HighlightEntry.Highlight> highlightList = itemHighlightEntry.getHighlights();
                if(highlightList != null && highlightList.size() > 0) {
                    for (HighlightEntry.Highlight highlight : highlightList) {
                        String hightTitle = highlight.getSnipplets().get(0);
                        item.setTitle(hightTitle);
                    }
                }
            }
        }
        //5、将结果集封装到map中
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", highlightPage.getTotalPages());  // 总页数
        map.put("total", highlightPage.getTotalElements());    // 总条数
        map.put("rows", highlightPage.getContent());           // 结果集
        return map;

    }

    //关键字检索并分页(没有实现高亮显示功能)
    private Map<String,Object> searchForPage(Map<String, String> searchMap) {
        //1、设置检索关键字
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)){
            criteria.is(keywords);
        }
        SimpleQuery query = new SimpleQuery(criteria);
        //2、设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer offSet = (pageNo - 1) * pageSize;
        query.setOffset(offSet);      //设置起始行
        query.setRows(pageSize);        //每页显示的条数
        //3、根据关键字检索并且结果分页
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
        //4、将结果集封装到map中
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", scoredPage.getTotalPages());  // 总页数
        map.put("total", scoredPage.getTotalElements());    // 总条数
        map.put("rows", scoredPage.getContent());           // 结果集
        return map;
    }
}
