package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mongo.MBillBackup;
import com.heji.server.data.mongo.repository.MBillBackupRepository;
import com.heji.server.service.BillBackupServer;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("BillBackupService")
public class BillBackupServiceImpl extends BaseMongoTemplate implements BillBackupServer {
    final MBillBackupRepository backupRepository;

    public BillBackupServiceImpl(MBillBackupRepository backupRepository) {
        this.backupRepository = backupRepository;
    }

    @Override
    public void backup(MBillBackup bill) {

        backupRepository.save(bill);
    }

    @Override
    public MBill getAllBacks(String book_id) {
        return null;
    }

    @Override
    public List<String> getAllBacksId(String book_id) {
        return backupRepository.findDistinctByBookId(book_id);
    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MBillBackup.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MBillBackup.COLLECTION_NAME);
            IndexOperations indexOpe = mongoTemplate.indexOps(MBillBackup.COLLECTION_NAME);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }
}
