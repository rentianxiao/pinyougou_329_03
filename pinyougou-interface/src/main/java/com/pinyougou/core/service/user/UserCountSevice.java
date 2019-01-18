package com.pinyougou.core.service.user;

import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.user.User;

public interface UserCountSevice {
    /*
     *    用户管理
     * */
    public PageResult search(Integer page, Integer rows, User user);
    /*
     *   更新状态
     * */
    public void  updateStatus(Long[] ids,String status);
    /*
     *    用户活跃度
     * */
    Integer sz();
}
