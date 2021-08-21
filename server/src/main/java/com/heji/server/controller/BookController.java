package com.heji.server.controller;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.MBookShare;
import com.heji.server.exception.OperationsException;
import com.heji.server.result.Result;
import com.heji.server.service.BookService;
import com.heji.server.service.BookShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController//json controller
@RequestMapping(path = "/book") // This means URL's start with /demo (after Application path)

public class BookController {
    final BookService bookService;
    final BookShareService bookShareService;

    public BookController(BookService bookService, BookShareService bookShareService) {
        this.bookService = bookService;
        this.bookShareService = bookShareService;
    }

    @ResponseBody
    @PostMapping(value = {"/create"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String createBook(@RequestBody MBook book, Authentication authentication) {
        book.setUsers(Collections.singletonList(authentication.getName()));
        bookService.createBook(book);
//        bookService.addBookUser(book, authentication.getName());
        return Result.success(book.get_id());
    }

    @ResponseBody
    @PostMapping(value = {"/getBooks"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBooks(Authentication authentication) {
        return Result.success(bookService.findBooks(authentication.getName()));
    }

    @ResponseBody
    @PostMapping(value = {"/addBookUser"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addBookUser(@RequestParam String bookId,Authentication auth) {
        bookService.addBookUser(new MBook().set_id(bookId), auth.getName());
        return Result.success(bookId);
    }

    @ResponseBody
    @PostMapping(value = {"/removeBookUser"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String removeBookUser(@RequestParam String bookId, @RequestParam String userId, Authentication auth) {
        //校验操作用户是否是账本创建人
        if (auth.getName() != bookService.findBook(bookId).getCreateUser()) {
            throw new OperationsException("移除失败，账本权限不匹配");
        }
        bookService.removeBookUser(new MBook().set_id(bookId), userId);
        return Result.success(bookId);
    }

    @ResponseBody
    @PostMapping(value = {"/share"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String shareBook(@RequestParam String bookId, Authentication auth) {
        //校验操作用户是否是账本创建人
        String createUser = bookService.findBook(bookId).getCreateUser();
        if (!auth.getName().equals(createUser)) {
            throw new OperationsException("分享失败，账本权限不匹配");
        }
        String code = bookShareService.generateCode(bookId);
        return Result.success(code);
    }

    @ResponseBody
    @PostMapping(value = {"/getShareBook"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getShareBook(@RequestParam String code, Authentication auth) {
        MBookShare shareBook = bookShareService.getShareBook(code);
        return Result.success(shareBook);
    }
}
