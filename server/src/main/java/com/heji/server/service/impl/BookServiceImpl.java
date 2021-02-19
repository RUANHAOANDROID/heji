package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.repository.MBookRepository;
import com.heji.server.service.BookService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;

@Service("BookService")
public class BookServiceImpl extends BaseMongoTemplate implements BookService {
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

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MBook.COLLATION_NAME)) {
            mongoTemplate.createCollection(MBook.COLLATION_NAME);
            IndexOperations indexOpe = mongoTemplate.indexOps(MBook.COLLATION_NAME);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }
}
