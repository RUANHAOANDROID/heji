package com.heji.server.controller;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.MBookUser;
import com.heji.server.data.mongo.MUser;
import com.heji.server.module.UserInfo;
import com.heji.server.result.Result;
import com.heji.server.service.BookService;
import com.heji.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
@Slf4j
public class UserController {
    final
    UserService userService;
    final BookService bookService;

    public UserController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    @ResponseBody
    @PostMapping(value = {"/register"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String register(@RequestBody UserInfo userInfo) {
        MUser mUser = new MUser()
                .setName(userInfo.getUsername())
                .setPassword(userInfo.getPassword())
                .setTel(userInfo.getTel())
                .setCode(userInfo.getCode());
//        MUser user1 =userService.findByTel(userInfo.getTel());
//        if (!StringUtils.isEmpty(user1.getTel())) {
//            throw new UserException("电话号码：" + user1.getTel() + "已存在");
//        }
        MBook mFirstBook =bookService.createFirstBook(mUser);
        mUser.setFirstBookId(mFirstBook.get_id());
        userInfo.setFirstBookId(mFirstBook.get_id());
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
    public String getUserId(@RequestHeader String token) {
        if (token.trim().contains("Bearer"))
            token =token.split("Bearer")[1];
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
