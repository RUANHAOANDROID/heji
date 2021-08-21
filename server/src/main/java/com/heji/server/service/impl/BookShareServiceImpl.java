package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MBookShare;
import com.heji.server.data.mongo.repository.MBookShareRepository;
import com.heji.server.service.BookShareService;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("BookShareService")
public class BookShareServiceImpl extends BaseMongoTemplate implements BookShareService {
    final MBookShareRepository repository;

    public BookShareServiceImpl(MBookShareRepository repository) {
        this.repository = repository;
    }


    @Override
    public String generateCode(String bookId) {
        String code = bookId.substring(bookId.length() - 5);//取object id 后五位
        MBookShare share = new MBookShare()
                .setBookId(bookId)
                .setExpiredTime(new Date())//过期时间3天
                .setCode(code);
        repository.save(share);
        return code;
    }

    @Override
    public MBookShare getShareBook(String code) {
        return repository.findMBookShareByCode(code);
    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MBookShare.COLLATION_NAME)) {
            mongoTemplate.createCollection(MBookShare.COLLATION_NAME);
//            IndexOperations indexOpe = mongoTemplate.indexOps(MBookShare.COLLATION_NAME);
//            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
//            //邀请保留有效期，效能同：@Indexed(name = "expiredTime" ,expireAfterSeconds = 60)
            getMongoTemplate().getCollection(MBookShare.COLLATION_NAME).
                    createIndex(Indexes.ascending("expiredTime"),
                            new IndexOptions().expireAfter(7L, TimeUnit.DAYS));
        }
    }
}
