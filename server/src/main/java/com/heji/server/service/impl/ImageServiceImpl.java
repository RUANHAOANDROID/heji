package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MBillImage;
import com.heji.server.data.mongo.repository.MImageRepository;
import com.heji.server.service.ImageService;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

@Service("ImageService")
public class ImageServiceImpl extends BaseMongoTemplate implements ImageService {
    final MImageRepository mImageRepository;

    public ImageServiceImpl(MImageRepository mImageRepository) {
        this.mImageRepository = mImageRepository;
    }

    @Override
    public String saveImage(MBillImage image) {
        String _id = getMongoTemplate().save(image, MBillImage.COLLECTION_NAME).get_id().toString();
        return _id;
    }

    @Override
    public MBillImage getImage(String imgId) {
        GridFsTemplate gridFsTemplate = getGridFsTemplate();
        Criteria cr = Criteria.where("_id").is(imgId);
        Query query = Query.query(cr);
        MBillImage mBillImage = getMongoTemplate().findOne(query, MBillImage.class, MBillImage.COLLECTION_NAME);
        //return mImageRepository.findById(imgId).get();
        return mBillImage;
    }

    @Override
    public boolean removeBillImages(String billId) {
        Criteria cr = Criteria.where("bill_id").is(billId);
        Query query = Query.query(cr);
        DeleteResult deleteResult = getMongoTemplate().remove(query, MBillImage.COLLECTION_NAME);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public void removeImages(String... imageId) {

    }

    @Override
    public void removeImage(String imageId) {

    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MBillImage.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MBillImage.COLLECTION_NAME);
            IndexOperations indexOpe = mongoTemplate.indexOps(MBillImage.COLLECTION_NAME);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }
}
