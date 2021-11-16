package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MErrorLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MErrorLogRepository extends MongoRepository<MErrorLog, String> {


}
