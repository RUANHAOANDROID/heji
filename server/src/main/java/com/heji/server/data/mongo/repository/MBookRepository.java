package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBook;
import com.heji.server.data.mongo.MCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MBookRepository extends MongoRepository<MBook, String> {

    List<MBook> findMBookByUsers(String userId);

    boolean existsMBookBy_id(String book_id);
}
