package com.heji.server.controller;

import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mongo.MUser;
import com.heji.server.module.UserInfo;
import com.heji.server.result.Result;
import com.heji.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                .setCode(userInfo.getCode());
        userService.register(mUser);
        return Result.success("注册成功");
    }

    @ResponseBody
    @PostMapping(value = {"/login"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String login(@RequestParam UserInfo userInfo) {
        MUser mUser = new MUser()
                .setName(userInfo.getTel())
                .setPassword(userInfo.getPassword())
                .setCode(userInfo.getCode());
        String auth =userService.login(mUser);
        return Result.success(auth);
    }

    @ResponseBody
    @PostMapping(value = {"/logout"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String logout(@RequestParam UserInfo userInfo) {
        MUser mUser = new MUser()
                .setName(userInfo.getTel())
                .setPassword(userInfo.getPassword())
                .setCode(userInfo.getCode());
        userService.register(mUser);
        return Result.success("注册成功");
    }

}
