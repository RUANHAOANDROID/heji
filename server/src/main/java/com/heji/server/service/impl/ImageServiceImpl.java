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

import java.util.List;

@Service("ImageService")
public class ImageServiceImpl extends BaseMongoTemplate implements ImageService {
    final MImageRepository mImageRepository;

    public ImageServiceImpl(MImageRepository mImageRepository) {
        this.mImageRepository = mImageRepository;
    }

    @Override
    public String saveImage(MBillImage image) {
        String _id = getMongoTemplate().save(image, MBillImage.COLLECTION_NAME).get_id();
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
    public List<MBillImage> getBillImages(String bill_id) {
        Criteria cr = Criteria.where("bill_id").is(bill_id);
        Query query = Query.query(cr);
        //排除图片文件数据
        query.fields().exclude("data")
//                .include("_id")
//                .include("bill_id")
//                .include("ext")
//                .include("length")
//                .include("bill_id")
//                .include("md5")
        ;
        return getMongoTemplate().find(query, MBillImage.class, MBillImage.COLLECTION_NAME);
    }

    @Override
    public boolean removeBillImages(String billId) {
        Criteria cr = Criteria.where("bill_id").is(billId);
        Query query = Query.query(cr);
        DeleteResult deleteResult = getMongoTemplate().remove(query, MBillImage.COLLECTION_NAME);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public boolean markAsDelete(String bill_id) {
        return false;
    }

    @Override
    public void removeImages(String... imageId) {

    }

    @Override
    public void removeImage(String imageId) {
        mImageRepository.deleteById(imageId);
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
