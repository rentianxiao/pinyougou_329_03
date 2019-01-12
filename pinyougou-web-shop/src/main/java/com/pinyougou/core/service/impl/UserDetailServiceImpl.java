package com.pinyougou.core.service.impl;

import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.core.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

//自定义认证管理器
@Service
public class UserDetailServiceImpl implements UserDetailsService{

    @Resource
    private SellerService sellerService;

    //属性注入
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * 认证用户并授权
     * @param
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Seller seller = sellerService.findOne(username);
        if (seller != null && "1".equals(seller.getStatus())){
            Set<GrantedAuthority> authorities = new HashSet<>();
            // 添加访问权限
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");
            authorities.add(grantedAuthority);
            User user = new User(username,seller.getPassword(),authorities);
            return user;
        }
        return null;
    }
}
