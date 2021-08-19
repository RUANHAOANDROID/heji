package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.repository.MBookRepository;
import com.heji.server.service.BookService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("BookService")
public class BookServiceImpl extends BaseMongoTemplate implements BookService {
    final MBookRepository mBookRepository;

    public BookServiceImpl(MBookRepository mBookRepository) {
        this.mBookRepository = mBookRepository;
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

    @Override
    public void createBook(MBook book) {
        mBookRepository.save(book);
    }

    @Override
    public void removeBook(String _id) {
        mBookRepository.delete(new MBook().set_id(_id));
    }

    @Override
    public MBook findBook(String _id) {
        return mBookRepository.findById(_id).get();
    }

    @Override
    public List<MBook> findBooks(String userId) {
        Query query = Query.query(Criteria.where("users").is(userId));
        List<MBook> myBooks = getMongoTemplate().find(query, MBook.class);
        //List<MBook> books2= mBookRepository.findMBookByUsers(userId);
        return myBooks;
    }

    @Override
    public MBook updateBook(MBook book) {
        return mBookRepository.save(book);
    }

    @Override
    public void addBookUser(MBook book, String userId) {
        Query query = Query.query(Criteria.where("_id").is(book.get_id()));
        Update update = new Update();
        update.addToSet("users", userId);
        getMongoTemplate().upsert(query, update, MBook.class);
    }

    @Override
    public void removeBookUser(MBook book, String userId) {
        Query query = Query.query(Criteria.where("_id").is(book.get_id()));
        Update update = new Update();
        update.pull("users", userId);
        getMongoTemplate().updateFirst(query, update, MBook.class);
    }

    @Override
    public boolean exists(String book_id) {
        return mBookRepository.existsMBookBy_id(book_id);
    }


}
