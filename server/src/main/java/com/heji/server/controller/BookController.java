package com.heji.server.controller;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.MBookShare;
import com.heji.server.data.mongo.MBookUser;
import com.heji.server.data.mongo.MOperateLog;
import com.heji.server.exception.NotFindException;
import com.heji.server.exception.OperationsException;
import com.heji.server.result.Result;
import com.heji.server.service.BookService;
import com.heji.server.service.BookShareService;
import com.heji.server.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        MBookUser bookUser = new MBookUser().setName(authentication.getName()).setAuthority("CREATE");
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
            throw new OperationsException("更新失败，账本权限不匹配");
        }
        bookService.updateBook(new MBook().set_id(bookId).setName(bookName).setType(bookType));
        return Result.success(bookId);
    }

    @ResponseBody
    @PostMapping(value = {"/deleteBook"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteBook(@RequestParam String bookId, Authentication auth) {
        if (bookService.exists(bookId)){
            throw new NotFindException("删除失败，账本不存在");
        }
        //校验操作用户是否是账本创建人
        if (auth.getName() != bookService.findBook(bookId).getUsers().get(0).getName()) {
            throw new OperationsException("删除失败，账本权限不匹配");
        }
        bookService.deleteBook(bookId);
        //写入操作日志
        operateLogService.addOperateLog(new MOperateLog()
                .setTargetId(bookId)
                .setType(MOperateLog.DELETE)
                .setDate(new Date())
                .setOptClass(MOperateLog.BOOK));
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
            throw new OperationsException("移除失败，账本权限不匹配");
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
            throw new OperationsException("分享失败，账本权限不匹配");
        }
        String code = bookShareService.generateCode(bookId);
        return Result.success(code);
    }
    @ResponseBody
    @PostMapping(value = {"/join"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String joinBook(@RequestParam String sharedCode, Authentication auth) {
        MBookUser bookUser = new MBookUser().setName(auth.getName()).setAuthority("USER");
        MBookShare sharedBook = bookShareService.getShareBook(sharedCode);
        bookService.joinBook(sharedBook.getBookId(), bookUser);
        return Result.success(sharedBook);
    }

}
