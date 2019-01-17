package com.pinyougou.core.service;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface ContentService {

	public List<Content> findAll();

	public PageResult findPage(Content content, Integer pageNum, Integer pageSize);

	public void add(Content content);

	public void edit(Content content);

	public Content findOne(Long id);

	public void delAll(Long[] ids);

	/**
	 * 前台系统大广告的轮播
	 * @param categoryId
	 * @return
	 */
	public List<Content> findByCategoryId(Long categoryId);

	/**
	 * 前台分类查询
	 * @param
	 * @return
	 */
	Map<String,Map<String,List<String>>> findAllItemCat();

	/**
	 * 查询品牌名称(楼层广告)
	 * @return
	 */
	List<Brand> findBrandName();
}
