package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MVerification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MVerificationRepository extends MongoRepository<MVerification, String> {
}
