package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBookShare;
import com.heji.server.data.mongo.MOperateLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MOperateLogRepository extends MongoRepository<MOperateLog, String> {

    List<MOperateLog> findMOperateLogsByTargetIdAndDateBetween(String targetId, String startDate, String endDate);

    List<MOperateLog> findMOperateLogsByTargetId(String targetId);

}
