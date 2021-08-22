package com.heji.server.service;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.MBookUser;

import java.util.List;

public interface BookService {
    void createBook(MBook book);

    void removeBook(String _id);

    MBook findBook(String _id);

    List<MBook> getBooks(MBookUser user);

    MBook updateBook(MBook book);

    void addBookUser(MBook book, MBookUser user);

    List<MBookUser> getBookUsers(String book_id);

    void removeBookUser(MBook book, String userId);

    boolean exists(String book_id);

}
