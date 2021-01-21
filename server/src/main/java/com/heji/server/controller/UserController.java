package com.heji.server.controller;

import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mongo.MUser;
import com.heji.server.exception.UserException;
import com.heji.server.module.UserInfo;
import com.heji.server.result.Result;
import com.heji.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/user")
@Slf4j
public class UserController {
    final
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping(value = {"/register"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String register(@RequestBody UserInfo userInfo) {
        MUser mUser = new MUser()
                .setName(userInfo.getTel())
                .setPassword(userInfo.getPassword())
                .setTel(userInfo.getTel())
                .setCode(userInfo.getCode());
        String tel = String.valueOf(userService.findByTel(userInfo.getTel()).getTel());
        if (!StringUtils.isEmpty(tel)) {
            throw new UserException("电话号码：" + tel + "已存在");
        }
        userService.register(mUser);
        return Result.success(userInfo);
    }

    @ResponseBody
    @PostMapping(value = {"/login"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String login(@RequestBody UserInfo userInfo) {
        MUser mUser = new MUser()
                .setName(userInfo.getTel())
                .setPassword(userInfo.getPassword())
                .setTel(userInfo.getTel())
                .setCode(userInfo.getCode());
        String auth = userService.login(mUser);
        return Result.success(auth);
    }
    @ResponseBody
    @PostMapping(value = {"/getUserId"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getUserId(@RequestParam String token) {
        String userID =userService.getUserId(token);
        return Result.success(userID);
    }

    @ResponseBody
    @PostMapping(value = {"/logout"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String logout(@RequestBody UserInfo userInfo) {
        MUser mUser = new MUser()
                .setName(userInfo.getTel())
                .setPassword(userInfo.getPassword())
                .setCode(userInfo.getCode());
        userService.register(mUser);
        return Result.success("注册成功");
    }

}
