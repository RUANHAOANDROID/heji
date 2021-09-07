package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MOperateLog;
import com.heji.server.data.mongo.repository.MOperateLogRepository;
import com.heji.server.service.OperateLogService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("OperateLogService")
public class OperateLogImpl extends BaseMongoTemplate implements OperateLogService {

    final MOperateLogRepository operateLogRepository;

    public OperateLogImpl(MOperateLogRepository operateLogRepository) {
        this.operateLogRepository = operateLogRepository;
    }

    @Override
    public void addOperateLog(MOperateLog object) {
        operateLogRepository.save(object);
    }

    @Override
    public List<MOperateLog> getDeleteLog(String book_id, String startDate, String endDate) {
        return operateLogRepository.findMOperateLogsByOpeID(book_id);
    }

    @Override
    public List<MOperateLog> getOperateLogs(String book_id) {
        Criteria criteria = Criteria.where("bookId").is(book_id).where("opeDate").gte("2021-08-01").lte("2021-09-31");
        Query query = new Query(criteria);
        query.fields().exclude("_id");
        List<MOperateLog> data= getMongoTemplate().find(query, MOperateLog.class, MOperateLog.COLLATION_NAME);
        List<MOperateLog> data2 =operateLogRepository.findMOperateLogsByBookIdAndOpeDateBetween(book_id,"2021-01-01","2021-12-01");
        return data2 ;
    }


    @Override
    public List<MOperateLog> getUpdateLog(String book_id, String startDate, String endDate) {
        return operateLogRepository.findMOperateLogsByOpeID(book_id);
    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MOperateLog.COLLATION_NAME)) {
            mongoTemplate.createCollection(MOperateLog.COLLATION_NAME);
            IndexOperations indexOpe = mongoTemplate.indexOps(MOperateLog.COLLATION_NAME);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }
}
