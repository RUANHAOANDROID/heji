package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MUser;
import com.heji.server.data.mongo.MVerification;
import com.heji.server.data.mongo.repository.MVerificationRepository;
import com.heji.server.service.VerificationService;
import org.bson.Document;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service("VerificationService")
public class VerificationServiceImpl extends BaseMongoTemplate implements VerificationService {

    final MVerificationRepository verifyRepository;

    public VerificationServiceImpl(MVerificationRepository verifyRepository) {
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
        for (int i = 0; i < 4 ; i++) {
            code+=random.nextInt(9);
        }
        return verifyRepository.save(new MVerification().setCode("123456")).getCode();
    }


    @Override
    public boolean existsCode(String code) {
        return verifyRepository.exists(Example.of(new MVerification().setCode(code)));
    }

    @Override
    public void deleteCode(String code) {
        verifyRepository.delete(new MVerification().setCode(code));
    }
}
