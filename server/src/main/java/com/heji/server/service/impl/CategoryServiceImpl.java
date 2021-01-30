package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MCategory;
import com.heji.server.data.mongo.repository.MCategoryRepository;
import com.heji.server.service.CategoryService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("CategoryService")
public class CategoryServiceImpl extends BaseMongoTemplate implements CategoryService {
    public static final String CATEGORY = "category";
    final MCategoryRepository mCategoryRepository;

    public CategoryServiceImpl(MCategoryRepository mCategoryRepository) {
        this.mCategoryRepository = mCategoryRepository;
    }

    @Override
    public String save(MCategory category) {
        MCategory mCategory = mCategoryRepository.save(category);
        return mCategory.get_id();
    }

    @Override
    public List<String> saveAll(List<MCategory> categories) {
        List<MCategory> categories1 = mCategoryRepository.saveAll(categories);
        return categories1.stream().map(category -> category.get_id()).collect(Collectors.toList());
    }

    @Override
    public MCategory find(String _id) {
        Optional<MCategory> optional = mCategoryRepository.findById(_id);
        MCategory mCategory = optional.get();
        return mCategory;
    }

    @Override
    public List<MCategory> findByBookId(String book_id) {
        return mCategoryRepository.findAll();
    }
    @Override
    public List<MCategory> findAll() {
        return mCategoryRepository.findAll();
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
    public boolean deleteByName(String name) {
        return mCategoryRepository.deleteByName(name)>0;
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
