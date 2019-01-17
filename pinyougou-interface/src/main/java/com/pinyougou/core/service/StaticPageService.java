package com.pinyougou.core.service;

public interface StaticPageService {

    /**
     * 生成商品详情静态页面
     * @param id
     */
    public void getStaticPage(Long id);

    /**
     * 删除页面
     * @param id
     */
    public boolean deleteFile(String id);
}
