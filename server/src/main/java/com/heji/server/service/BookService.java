package com.heji.server.service;

import com.heji.server.data.mongo.MBook;

public interface BookService {
    void addBook(MBook book);

    void removeBook(String _id);

    MBook findBook(String _id);

    MBook updateBook(MBook book);
}
