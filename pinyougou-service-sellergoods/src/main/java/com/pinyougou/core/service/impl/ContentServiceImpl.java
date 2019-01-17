package com.pinyougou.core.service.impl;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.core.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ContentDao contentDao;

	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	@Resource
	private ItemCatDao itemCatDao;

	@Resource
	private BrandDao brandDao;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getResult(), page.getTotal());
	}

	@Override
	@Transactional
	public void add(Content content) {
		//清空缓存数据
		clearCache(content.getCategoryId());
		contentDao.insertSelective(content);
	}

	@Transactional
	@Override
	public void edit(Content content) {
		//清空缓存数据
			//判断分类的id是否发生改变
		Long newCategoryId = content.getCategoryId();
		Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
		if (oldCategoryId != newCategoryId) {
			//分类id发生改变了：新的、老的分类id都清空
			clearCache(newCategoryId);
			clearCache(oldCategoryId);
		}else {
			clearCache(newCategoryId);
		}
		// 更新广告
		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Transactional
	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				//清空缓存数据
				clearCache(contentDao.selectByPrimaryKey(id).getCategoryId());
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	/**
	 * 前台系统大广告的轮播
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<Content> findByCategoryId(Long categoryId){
		// 首先判断缓存中是否有数据
		List<Content> list = (List<Content>) redisTemplate.boundHashOps("CONTENT").get(categoryId);
		if(list == null){
			synchronized (this){
				//二次校验，再次判断缓存中是否有数据
				list = (List<Content>) redisTemplate.boundHashOps("CONTENT").get(categoryId);
				if (list == null) {
					// 缓存中没有数据，从数据库中查询
					ContentQuery contentQuery = new ContentQuery();
					// 可用的广告：status=1
					contentQuery.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
					list = contentDao.selectByExample(contentQuery);
					// 将数据再次放入缓存中
					redisTemplate.boundHashOps("CONTENT").put(categoryId, list);
					// 设置缓存过期时间（可选）
					redisTemplate.boundHashOps("CONTENT").expire(1, TimeUnit.DAYS);
				}
			}
		}
		return list;
	}

	/**
	 * 前台分类查询
	 * @param
	 * @return
	 */
	@Override
	public Map<String, Map<String, List<String>>> findAllItemCat() {
		//在缓存中查询Map
		Map<String, Map<String, List<String>>> itemCatMap = (Map<String, Map<String, List<String>>>) redisTemplate.boundHashOps("findAllItemCat").get("findAllItemCat");
		//如果缓存中没有Map,则在数据库中查询
		if (itemCatMap == null) {
			itemCatMap = new HashMap<>();
			//查询一级分类,即父类ID为0
			ItemCatQuery itemCatQuery = new ItemCatQuery();
			itemCatQuery.createCriteria().andParentIdEqualTo(0L);
			//第一层
			List<ItemCat> itemCatList1 = itemCatDao.selectByExample(itemCatQuery);
			for (ItemCat itemCat : itemCatList1) {
				ItemCatQuery itemCatQuery1 = new ItemCatQuery();
				itemCatQuery1.createCriteria().andParentIdEqualTo(itemCat.getId());
				//第二层
				List<ItemCat> itemCatList2 = itemCatDao.selectByExample(itemCatQuery1);
				Map<String, List<String>> itemCatMap1 = new HashMap<>();
				for (ItemCat itemCat2 : itemCatList2) {
					ItemCatQuery itemCatQuery2 = new ItemCatQuery();
					itemCatQuery2.createCriteria().andParentIdEqualTo(itemCat2.getId());
					//第三层
					List<ItemCat> itemCatList3 = itemCatDao.selectByExample(itemCatQuery2);
					List<String> itemList = new ArrayList<>();
					for (ItemCat itemCat3 : itemCatList3) {
						//向List<String> itemList中添加数据
						itemList.add(itemCat3.getName());
					}
					itemCatMap1.put(itemCat2.getName(), itemList);
				}
				itemCatMap.put(itemCat.getName(), itemCatMap1);
			}
			//查询后将数据存入缓存
			redisTemplate.boundHashOps("findAllItemCat").put("findAllItemCat", itemCatMap);
		}
		return itemCatMap;
	}

	/**
	 * 查询品牌名称(楼层广告)
	 * @return
	 */
    @Override
    public List<Brand> findBrandName() {
		List<Brand> brands = (List<Brand>) redisTemplate.boundHashOps("findBrandName").get("findBrandName");
		if (brands == null) {
			brands = new ArrayList<>();
			List<Brand> brandList = brandDao.selectByExample(null);
			for (int i = 0; i < 6; i++) {
				Brand brand = brandList.get(i);
				brands.add(brand);
			}
			redisTemplate.boundHashOps("findBrandName").put("findBrandName", brands);
		}
		return brands;
    }

    /**
	 * //清空缓存数据
	 * @param categoryId
	 */
	private void clearCache(Long categoryId) {
		redisTemplate.boundHashOps("CONTENT").delete(categoryId);
	}
}
