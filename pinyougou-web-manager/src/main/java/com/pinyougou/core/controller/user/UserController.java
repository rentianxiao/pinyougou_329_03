package com.pinyougou.core.controller.user;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.entity.Result;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.core.service.user.UserCountSevice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserCountSevice userCountSevice;

    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody User user ){
        return userCountSevice.search(page, rows, user);
    }
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids, String status){
        try {
            userCountSevice.updateStatus(ids,status);
            return new Result(true,"冻结成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"冻结失败");
        }

    }

    /*
     *用户活跃度
     * */
    @RequestMapping("/sz.do")
    public  Integer sz(){
        return userCountSevice.sz();
    }
}
