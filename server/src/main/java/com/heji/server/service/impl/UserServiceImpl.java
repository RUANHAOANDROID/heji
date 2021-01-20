package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MBillImage;
import com.heji.server.data.mongo.MUser;
import com.heji.server.data.mongo.repository.MUserRepository;
import com.heji.server.exception.NotFindException;
import com.heji.server.service.UserService;
import com.heji.server.service.VerificationService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Service("UserService")
public class UserServiceImpl extends BaseMongoTemplate implements UserService {

    final VerificationService mVerificationService;
    final MUserRepository mUserRepository;
    final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(VerificationService verificationService, MUserRepository mUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.mVerificationService = verificationService;
        this.mUserRepository = mUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MUser.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MUser.COLLECTION_NAME);
            IndexOperations indexOpe = mongoTemplate.indexOps(MUser.COLLECTION_NAME);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }

    @Override
    public void register(MUser mUser) {
        String code = mUser.getCode();//邀请码
        boolean exists = mVerificationService.existsCode(code);//存在
        if (exists) {
            String password = bCryptPasswordEncoder.encode(mUser.getPassword());//加密

            mUser.setPassword(password);//存入
            MUser newUser = mUserRepository.save(mUser);
            mVerificationService.deleteCode(code);
        } else {
            throw new NotFindException(" verification not find");
        }
    }

    @Override
    public void update(MUser mUser) {
        if (Objects.nonNull(mUser)) {
            mUserRepository.save(mUser);
        }
    }

    @Override
    public String login(MUser mUser) {
        MUser user1 = mUserRepository.findById(mUser.get_id()).get();
        user1.getName().equals(mUser.getName());
        user1.getPassword().equals(mUser.getPassword());
        boolean isOk = bCryptPasswordEncoder.matches(mUser.getPassword(), user1.getPassword());
        Properties props = new Properties();
        props.put("_id", mUser.get_id());
        props.put("name", mUser.getName());
        props.put("tel", mUser.getTel());
        String auth = getCurrentUsername();
        return auth;
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        if (principal instanceof Principal) {
            return ((Principal) principal).getName();
        }
        return String.valueOf(principal);
    }

    @Override
    public void logout(MUser mUser) {

    }

    @Override
    public MUser findByName(String username) {
        Criteria criteria = Criteria.where("name").is(username);
        Query query = new Query(criteria);
        return getMongoTemplate().findOne(query, MUser.class);
    }
}
