package com.heji.server.controller;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.MBookShare;
import com.heji.server.data.mongo.MBookUser;
import com.heji.server.data.mongo.MOperateLog;
import com.heji.server.exception.NotFoundException;
import com.heji.server.exception.OperationException;
import com.heji.server.result.Result;
import com.heji.server.service.BookService;
import com.heji.server.service.BookShareService;
import com.heji.server.service.OperateLogService;
import com.heji.server.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController//json controller
@RequestMapping(path = "/book") // This means URL's start with /demo (after Application path)

public class BookController {
    final BookService bookService;
    final BookShareService bookShareService;
    final OperateLogService operateLogService;

    public BookController(BookService bookService, BookShareService bookShareService, OperateLogService operateLogService) {
        this.bookService = bookService;
        this.bookShareService = bookShareService;
        this.operateLogService = operateLogService;
    }

    @ResponseBody
    @PostMapping(value = {"/create"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String createBook(@RequestBody MBook book, Authentication authentication) {
        List<MBookUser> users = new ArrayList<>();
        MBookUser bookUser = new MBookUser()
                .setName(authentication.getName())
                .setAuthority(MBookUser.AUTHORITYS[0]);
        book.setFirstBook(1);//not firstBook
        users.add(bookUser);
        book.setUsers(users);
        bookService.createBook(book);
        return Result.success(book.get_id());
    }

    @ResponseBody
    @PostMapping(value = {"/getBooks"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBooks(Authentication authentication) {
        return Result.success(bookService.getBooks(new MBookUser().setName(authentication.getName())));
    }

    @ResponseBody
    @PostMapping(value = {"/findBook"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBookById(@RequestParam String bookId) {
        return Result.success(bookService.findBook(bookId));
    }


    @ResponseBody
    @PostMapping(value = {"/updateBook"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String updateBook(@RequestParam String bookId,
                             @RequestParam String bookName,
                             @RequestParam String bookType,
                             Authentication auth) {
        //校验操作用户是否是账本创建人
        if (auth.getName() != bookService.findBook(bookId).getUsers().get(0).getName()) {
            throw new OperationException("更新失败，账本权限不匹配");
        }
        bookService.updateBook(new MBook().set_id(bookId).setName(bookName).setType(bookType));
        operateLogService.addOperateLog(new MOperateLog()
                .setBookId(bookId)
                .setOpeDate(TimeUtils.getNowString())
                .setOpeClass(MOperateLog.BOOK)
                .setOpeType(MOperateLog.UPDATE));
        return Result.success(bookId);
    }

    @ResponseBody
    @DeleteMapping(value = {"/deleteBook"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteBook(@RequestParam String bookId, Authentication auth) {
        if (!bookService.exists(bookId)){
            throw new NotFoundException("删除失败，账本不存在");
        }
        //校验操作用户是否是账本创建人
        MBook book = bookService.findBook(bookId);
        if (!auth.getName().equals(book.getUsers().get(0).getName())) {
            throw new OperationException("删除失败，账本权限不匹配");
        }
        bookService.deleteBook(bookId);
        //写入操作日志
        operateLogService.addOperateLog(new MOperateLog()
                .setBookId(bookId)
                .setOpeID(bookId)
                .setOpeType(MOperateLog.DELETE)
                .setOpeDate(TimeUtils.getNowString())
                .setOpeClass(MOperateLog.BOOK));
        return Result.success(bookId);
    }

    @ResponseBody
    @PostMapping(value = {"/getBookUsers"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBookUsers(@RequestParam String bookId, Authentication auth) {
        //校验操作用户是否是账本创建人
        List<MBookUser> users = bookService.getBookUsers(bookId);
        return Result.success(users);
    }

    @ResponseBody
    @PostMapping(value = {"/removeBookUser"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String removeBookUser(@RequestParam String bookId, @RequestParam String userId, Authentication auth) {
        //校验操作用户是否是账本创建人
        if (auth.getName() != bookService.findBook(bookId).getUsers().get(0).getName()) {
            throw new OperationException("移除失败，账本权限不匹配");
        }
        bookService.removeBookUser(new MBook().set_id(bookId), userId);
        return Result.success(bookId);
    }

    @ResponseBody
    @PostMapping(value = {"/shared"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String sharedBook(@RequestParam String bookId, Authentication auth) {
        //校验操作用户是否是账本创建人
        String createUser = bookService.findBook(bookId).getUsers().get(0).getName();
        if (!auth.getName().equals(createUser)) {
            throw new OperationException("分享失败，账本权限不匹配");
        }
        String code = bookShareService.generateCode(bookId);
        return Result.success(code);
    }

    @ResponseBody
    @PostMapping(value = {"/join"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String joinBook(@RequestParam String sharedCode, Authentication auth) {
        MBookUser bookUser = new MBookUser().setName(auth.getName()).setAuthority("USER");
        MBookShare sharedBook = bookShareService.getShareBook(sharedCode);
        if (Objects.isNull(sharedBook)) throw new OperationException("加入账本失败，请核对邀请码");
        bookService.joinBook(sharedBook.getBookId(), bookUser);
        return Result.success(sharedBook.getBookId());
    }

}
