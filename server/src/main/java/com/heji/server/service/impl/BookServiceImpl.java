package com.heji.server.service.impl;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.repository.MBookRepository;
import com.heji.server.service.BookService;
import org.springframework.stereotype.Service;

@Service("BookService")
public class BookServiceImpl implements BookService {
    final MBookRepository mBookRepository;

    public BookServiceImpl(MBookRepository mBookRepository) {
        this.mBookRepository = mBookRepository;
    }

    @Override
    public void addBook(MBook book) {
        mBookRepository.save(book);
    }

    @Override
    public void removeBook(String _id) {
        mBookRepository.deleteById(_id);
    }

    @Override
    public MBook findBook(String _id) {
        return mBookRepository.findById(_id).get();
    }

    @Override
    public MBook updateBook(MBook book) {
        return mBookRepository.save(book);
    }
}
