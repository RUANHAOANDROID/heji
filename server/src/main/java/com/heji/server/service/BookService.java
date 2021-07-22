package com.heji.server.service;

import com.heji.server.data.mongo.MBook;

import java.util.List;

public interface BookService {
    void createBook(MBook book);

    void removeBook(String _id);

    MBook findBook(String _id);

    List<MBook> findBooks(String userId);

    MBook updateBook(MBook book);

    void addBookUser(MBook book, String userId);
    void removeBookUser(MBook book, String userId);
}
