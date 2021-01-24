package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MVerification;
import com.heji.server.data.mongo.repository.MVerificationRepository;
import com.heji.server.service.CodeService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service("VerificationService")
public class CodeServiceImpl extends BaseMongoTemplate implements CodeService {

    final MVerificationRepository verifyRepository;

    public CodeServiceImpl(MVerificationRepository verifyRepository) {
        this.verifyRepository = verifyRepository;
    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MVerification.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MVerification.COLLECTION_NAME);
            IndexOperations indexOpe = mongoTemplate.indexOps(MVerification.COLLECTION_NAME);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }

    @Override
    public String createCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 4; i++) {
            code += random.nextInt(9);
        }
        return verifyRepository.save(new MVerification().setCode("123456")).getCode();
    }


    @Override
    public boolean existsCode(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        return getMongoTemplate().exists(query, MVerification.class, MVerification.COLLECTION_NAME);
    }

    @Override
    public void deleteCode(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        getMongoTemplate().remove(query, MVerification.class, MVerification.COLLECTION_NAME);
    }
}
