package com.pinyougou.core.controller.user;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.PhoneFormatCheckUtils;

import java.util.List;
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


    /**
     * 2019-1-14订单的分页查询
     * @param page
     * @param rows
     * @param order
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows , @RequestBody Order order) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.search(userName,page,rows,order);
    }

    /**
     * 用户信息查询
     * @return
     */
    @RequestMapping("/findUser.do")
    public List<User> findUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUser(userName);
    }
}
