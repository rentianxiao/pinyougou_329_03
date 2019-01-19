package com.pinyougou.core.service.user;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.entity.PageResult;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserCountSeviceImpl implements UserCountSevice {

    @Resource
    private UserDao userDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, User user) {
        PageHelper.startPage(page, rows);
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();
        if (user.getUsername() != null && !"".equals(user.getUsername().trim())) {
            criteria.andNameLike("%" + user.getUsername().trim() + "%");
        }
        //根据条件查询
        Page<User> users = (Page<User>) userDao.selectByExample(userQuery);
        PageResult pageResult = new PageResult(users.getResult(), users.getTotal());
        return pageResult;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                UserQuery userQuery = new UserQuery();
                userQuery.createCriteria().andIdEqualTo(id);
                List<User> userList = userDao.selectByExample(userQuery);
                for (User user : userList) {
                    user.setStatus(status);
                    userDao.updateByPrimaryKeySelective(user);
                }
            }
        }
    }

    /**
     * 用户活跃度
     * @return
     */
    @Override
    public Integer sz() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String format1 = format.format(date);
        Integer num=0;
        for (int i = 1; i < 30; i++) {
            Boolean sz = redisTemplate.opsForValue().getBit("sz"+format1, i);
            if (sz) {
                num++;
            }
        }
        return  num;
    }


    /*
     *    搜索
     * */

}
