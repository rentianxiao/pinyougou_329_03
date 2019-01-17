package com.pinyougou.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 前台系统大广告的轮播
     *
     * @param categoryId
     * @return
     */
    @RequestMapping("/findByCategoryId.do")
    public List<Content> findByCategoryId(Long categoryId) {
        return contentService.findByCategoryId(categoryId);
    }

    /**
     * 前台分类查询
     *
     * @param
     * @return
     */
    @RequestMapping("/findAllItemCat.do")
    public Map<String, Map<String, List<String>>> findAllItemCat() {
        return contentService.findAllItemCat();
    }

    /**
     * 查询6个品牌名称
     * @return
     */
    @RequestMapping("/findBrandName.do")
    public List<Brand> findBrandName() {
        return contentService.findBrandName();
    }
}
