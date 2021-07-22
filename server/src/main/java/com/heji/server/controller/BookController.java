package com.heji.server.controller;

import com.heji.server.data.mongo.MBook;
import com.heji.server.result.Result;
import com.heji.server.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController//json controller
@RequestMapping(path = "/book") // This means URL's start with /demo (after Application path)

public class BookController {
    final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ResponseBody
    @PostMapping(value = {"/create"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String createBook(@RequestBody MBook book) {
        bookService.createBook(book);
        return Result.success(book.get_id());
    }
    @ResponseBody
    @PostMapping(value = {"/getBooks"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBooks(@RequestParam String userId) {
        return Result.success(bookService.findBooks(userId));
    }

    @ResponseBody
    @PostMapping(value = {"/addBookUser"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String addBookUser(@RequestParam String bookId,@RequestParam String userId) {
        bookService.addBookUser(new MBook().set_id(bookId),userId);
        return Result.success(bookId);
    }
    @ResponseBody
    @PostMapping(value = {"/removeBookUser"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String removeBookUser(@RequestParam String bookId,@RequestParam String userId) {
        bookService.removeBookUser(new MBook().set_id(bookId),userId);
        return Result.success(bookId);
    }
}
