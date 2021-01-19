package com.heji.server.service;

public interface VerificationService {
    //管理员创建一个随机码
    String createCode();

    //用户注册查找该码
    boolean existsCode(String code);

    //注册完成后删除该码
    void deleteCode(String code);
}
