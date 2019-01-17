package com.pinyougou.core.controller;

import cn.itcast.core.pojo.entity.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.ItemSearchService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search.do")
    public Map<String, Object> search(@RequestBody Map<String, String> searchMap) {
        return itemSearchService.search(searchMap);
    }

    @RequestMapping("/addToCollection.do")
    public Result addToCollection(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(username)) {
            String status = "";
            try {
                status = itemSearchService.addToCollection(id, username);
                return new Result(true, status+"收藏成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, status+"收藏失败");
            }
        } else {
            return new Result(false, "请先登录");
        }

    }
}
