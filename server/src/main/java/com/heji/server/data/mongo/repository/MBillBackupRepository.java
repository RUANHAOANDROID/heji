package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBillBackup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MBillBackupRepository extends MongoRepository<MBillBackup, String> {
    @Query
    List<String> findDistinctByBookId(String book_id);
}
