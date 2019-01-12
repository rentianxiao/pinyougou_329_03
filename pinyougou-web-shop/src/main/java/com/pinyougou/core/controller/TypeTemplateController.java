package com.pinyougou.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    /**
     * 根据模板ID加载除对应的品牌以及扩展属性
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    /**
     * 通过模板ID加载出对应的规格规格以及规格选项
     * @param id
     * @return
     */
    @RequestMapping("/findBySpecList.do")
    public List<Map> findBySpecList(Long id){
        return typeTemplateService.findBySpecList(id);
    }

}
