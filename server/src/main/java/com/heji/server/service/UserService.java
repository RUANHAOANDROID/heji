package com.heji.server.service;

import com.heji.server.data.mongo.MUser;

public interface UserService {
    void register(MUser mUser);

    void update(MUser mUser);

    String login(MUser mUser);

    void logout(MUser mUser);

    MUser findByName(String username);
}
