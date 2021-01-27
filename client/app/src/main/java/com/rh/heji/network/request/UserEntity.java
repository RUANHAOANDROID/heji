package com.rh.heji.network.request;

import com.rh.heji.data.db.Dealer;

/**
 * Date: 2020/9/24
 * Author: 锅得铁
 * #
 */
public class UserEntity {
    private String userId;

    private String userName;

    private String userCode;

    public UserEntity(Dealer dealer) {
        this.userName = dealer.getUserName();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
