package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBookShare;
import com.heji.server.data.mongo.MOperateLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MOperateLogRepository extends MongoRepository<MOperateLog, String> {
    List<MOperateLog> findMOperateLogsByOpeID(String optId);
    List<MOperateLog> findMOperateLogsByBookId(String bookId);
    List<MOperateLog> findMOperateLogsByBookIdAndOpeDateBetween(String bookId,String startDate,String endDate);
}
