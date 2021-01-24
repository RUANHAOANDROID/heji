package com.heji.server.service;

import com.heji.server.data.mongo.MUser;

public interface UserService {
    void register(MUser mUser);

    void update(MUser mUser);

    String login(String username,String password );

    String getUserId(String token);

    void logout(String mUser);

    MUser findByName(String username);

    MUser findByTel(String tel);
}
