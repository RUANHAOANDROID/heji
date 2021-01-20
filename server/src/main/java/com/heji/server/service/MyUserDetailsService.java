package com.heji.server.service;

import com.heji.server.data.mongo.MUser;
import com.heji.server.data.mongo.repository.MUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("UserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    private final MUserRepository userRepository;

    public MyUserDetailsService(MUserRepository userService) {
        this.userRepository = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) {
        MUser mUser =userRepository.findMUserByName(userName);
        Set<String> userRoles=new HashSet<>();;
        List<GrantedAuthority> authorities = getUserAuthority(userRoles);
        return buildUserForAuthentication(userName,mUser.getPassword(), authorities);
    }

    /**
     * 获取用户权限
     * @param userRoles
     * @return
     */
    private List<GrantedAuthority> getUserAuthority(Set<String> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (String role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(String name,String password , List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(name, password, authorities);
    }
}