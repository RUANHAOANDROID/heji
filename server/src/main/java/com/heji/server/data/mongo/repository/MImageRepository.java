package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBillImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MImageRepository extends MongoRepository<MBillImage, String> {
}
