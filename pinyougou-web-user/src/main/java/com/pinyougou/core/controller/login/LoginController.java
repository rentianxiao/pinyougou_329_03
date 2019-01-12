package com.pinyougou.core.controller.login;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 显示当前登录人名称
     * @return
     */
    @RequestMapping("/name.do")
    public Map<String,String> showName(){
        //从当前spring容器中取出当前用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,String> map = new HashMap<>();
        map.put("loginName",username);
        return map;
    }
}
