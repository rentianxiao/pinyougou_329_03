package com.pinyougou.core.service;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.entity.PageResult;

import java.util.List;

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

}
