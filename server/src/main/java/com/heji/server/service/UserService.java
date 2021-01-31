package com.heji.server.service;

import com.heji.server.data.mongo.MUser;
import org.springframework.security.core.userdetails.User;

public interface UserService {
    void register(MUser mUser);

    void update(MUser mUser);

    String login(String username, String password);

    User getUserId(String token);

    void logout(String mUser);

    MUser findByName(String username);

    MUser findByTEL(String tel);

    MUser findByTel(String tel);
}
