package com.pinyougou.core.service;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 前台系统的检索
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map<String,String> searchMap);

    /**
     * 将商品信息保存到索引库中
     * @param id
     */
    public void updateSolr(Long id);

    /**
     * 删除索引库中商品
     * @param id
     */
    public void deleteItemFromSolr(Long id);

    /**
     * 加入收藏
     * @param id
     */
    String addToCollection(Long id , String username);
}
