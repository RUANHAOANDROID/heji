package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MCategoryRepository extends MongoRepository<MCategory, String> {

}
