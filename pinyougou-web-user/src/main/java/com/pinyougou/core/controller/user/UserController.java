package com.pinyougou.core.controller.user;

import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.PhoneFormatCheckUtils;

import java.util.regex.PatternSyntaxException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @RequestMapping("/sendCode.do")
    public Result sendCode(String phone){
        try {
            // 校验手机号是否合法
            boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
            if(!phoneLegal){
                return new Result(false, "该手机号不合法");
            }
            userService.sendCode(phone);
            return new Result(true, "发送成功");
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
            return new Result(false, "发送失败");
        }
    }

    /**
     * 用户注册
     * @param smscode
     * @param user
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(String smscode ,@RequestBody User user){
        try {
            userService.add(smscode, user);
            return new Result(true, "注册成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "注册失败");
        }
    }
}
