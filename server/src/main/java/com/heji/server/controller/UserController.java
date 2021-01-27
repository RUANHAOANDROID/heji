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
import org.springframework.security.core.userdetails.User;
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
//        MUser user1 =userService.findByTel(userInfo.getTel());
//        if (!StringUtils.isEmpty(user1.getTel())) {
//            throw new UserException("电话号码：" + user1.getTel() + "已存在");
//        }
        userService.register(mUser);
        return Result.success(userInfo);
    }

    @ResponseBody
    @PostMapping(value = {"/login"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String login(@RequestParam String  username,@RequestParam String password) {
        String auth = userService.login(username,password);
        return Result.success("登陆成功",auth);
    }
    @ResponseBody
    @PostMapping(value = {"/auth"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getUserId(@RequestParam String token) {
        User user =userService.getUserId(token);
        return Result.success(user);
    }

    @ResponseBody
    @PostMapping(value = {"/logout"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String logout(@RequestParam String token) {

        userService.logout(token);
        return Result.success("退出成功");
    }

}
