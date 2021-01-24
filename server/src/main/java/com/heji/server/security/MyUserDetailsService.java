package com.heji.server.security;

import com.heji.server.data.mongo.MUser;
import com.heji.server.data.mongo.repository.MUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("UserDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    final MUserRepository mUserRepository;

    public MyUserDetailsService(MUserRepository mUserRepository) {
        this.mUserRepository = mUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        MUser mUser =mUserRepository.findMUserByName(s);
        return User.builder()
                .username(mUser.getName())
                .password(mUser.getPassword())
                .authorities(mUser.getAuthority())
                .build();
    }
}
