package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBillImage;
import com.heji.server.data.mongo.MUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MUserRepository extends MongoRepository<MUser, String> {
    @Query
    MUser findMUserByName(String name);
}
