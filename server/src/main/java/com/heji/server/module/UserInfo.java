package com.heji.server.module;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.MUser;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@ToString
@Accessors(chain = true)
public class UserInfo implements UserDetails {
    @Id
    String _id;
    String name;
    String password;
    String tel;
    String role;//角色权限
    @Ignore
    String code;//邀请码

    String firstBookId;

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public static UserInfo create(MUser user) {
        List<String> roles =new ArrayList<>();
        List<GrantedAuthority> authorities = roles.stream().map(role ->
                    new SimpleGrantedAuthority(user.getName())
        ).collect(Collectors.toList());

        return new UserInfo()
                .set_id(user.get_id())
                .setName(user.getName())
                .setPassword(user.getPassword())
                .setCode(user.getCode())
                .setTel(user.getTel())
                .setAuthorities(authorities)
                ;
    }


    @Override
    public String getUsername() {
        return tel;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(_id, userInfo._id) &&
                Objects.equals(name, userInfo.name) &&
                Objects.equals(tel, userInfo.tel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, name, tel);
    }
}
