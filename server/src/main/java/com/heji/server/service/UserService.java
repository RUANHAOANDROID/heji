package com.heji.server.service;

import com.heji.server.data.mongo.MUser;

public interface UserService {
    void register(MUser mUser);

    void update(MUser mUser);

    String login(MUser mUser);

    String getUserId(String token);

    void logout(String mUser);

    MUser findByName(String username);

    MUser findByTel(String tel);
}
