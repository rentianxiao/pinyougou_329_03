package com.pinyougou.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义的springsecurity与cas整合后的认证类
 */
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * 进入到该方法：说明cas已完对该用户的认证，
     * 因此在这里我们只需要对当前用户进行授权
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过springsecurity对该用户进行授权，认证的工作交给cas去完成了
        Set<GrantedAuthority> authorities = new HashSet<>();
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        authorities.add(grantedAuthority);
        User user = new User(username, "", authorities);
        return user;
    }
}
