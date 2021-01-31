package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBillImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MImageRepository extends MongoRepository<MBillImage, String> {
    //字段更名"db.bill_image.updateMany({"ext":".jpg"}, {$rename:{"ext":"extt"}})"
}
