package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBillImage;
import com.heji.server.data.mongo.MUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MUserRepository extends MongoRepository<MUser, String> {
}
