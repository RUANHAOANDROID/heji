package com.heji.server.service.impl;

import com.heji.server.data.mongo.AbstractBaseMongoTemplate;
import com.heji.server.data.mongo.MCategory;
import com.heji.server.data.mongo.repository.MCategoryRepository;
import com.heji.server.service.CategoryService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("CategoryService")
public class CategoryServiceImpl extends AbstractBaseMongoTemplate implements CategoryService {
    public static final String CATEGORY = "category";
    final MCategoryRepository mCategoryRepository;

    public CategoryServiceImpl(MCategoryRepository mCategoryRepository) {
        this.mCategoryRepository = mCategoryRepository;
    }

    @Override
    public String save(MCategory category) {
        MCategory mCategory = mCategoryRepository.save(category);
        return mCategory.get_id().toString();
    }

    @Override
    public MCategory find(String _id) {
        Optional<MCategory> optional = mCategoryRepository.findById(_id);
        MCategory mCategory = optional.get();
        return mCategory;
    }

    @Override
    public List<MCategory> findByBookId(String book_id) {
        return null;
    }

    @Override
    public String update(MCategory category) {
        return null;
    }

    @Override
    public boolean delete(String _id) {
        mCategoryRepository.deleteById(_id);
        return true;
    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(CATEGORY)) {
            mongoTemplate.createCollection(CATEGORY);
            IndexOperations indexOpe = mongoTemplate.indexOps(CATEGORY);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));//建立哈希索引
        }
    }
}
