package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBookShare;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MBookShareRepository extends MongoRepository<MBookShare, String> {
    MBookShare findMBookShareByCode(String code);
}
