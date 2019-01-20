package com.pinyougou.core.controller.order;



import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.core.service.OrderService;
import com.sun.tools.internal.ws.processor.model.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/zhuzhuangtu")
public class OldItemController  {
    @Reference
    private OrderService orderService;


   /* @RequestMapping("/find.do")
    public Map<String,ArrayList<String>>FindBiao(){
        Map<String, ArrayList<String>> map = orderService.findOne();
        System.out.println(map);
        return map;
    }*/
   @RequestMapping("/find.do")
   public String FindBiao(){

       String s = JSON.toJSONString(orderService.findOne());
       //System.out.println(s);
      String T = "[{\"value\":335, \"name\":\"直接访问\"},\n" +
              "  {\"value\":310, \"name\":\"邮件营销\"},\n" +
              "  {\"value\":234, \"name\":\"联盟广告\"},\n" +
              "  {\"value\":135, \"name\":\"视频广告\"},\n" +
              "  {\"value\":1548, \"name\":\"搜索引擎\"}\n" +
              "]";
       return s;
   }
    @RequestMapping("/findName.do")
    public Object FindName() {
        List name = orderService.findName();
        //System.out.println(name);
        JSONArray array= JSONArray.parseArray(JSON.toJSONString(name));
        //System.out.println(array.toString());

        return array;

    }

    @RequestMapping("/findShop.do")
    public String findShop(String name){
        //System.out.println(name);
        String shop = JSON.toJSONString(orderService.findShop(name));
        return shop;
    }




}
