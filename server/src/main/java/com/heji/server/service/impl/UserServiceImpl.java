package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MUser;
import com.heji.server.data.mongo.repository.MUserRepository;
import com.heji.server.exception.NotFindException;
import com.heji.server.service.UserService;
import com.heji.server.service.VerificationService;
import org.bson.Document;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.Ticket;

import javax.websocket.Session;
import java.net.PasswordAuthentication;
import java.util.Objects;
import java.util.Properties;

@Service("UserService")
public class UserServiceImpl extends BaseMongoTemplate implements UserService {

    final VerificationService mVerificationService;
    final MUserRepository mUserRepository;

    public UserServiceImpl(VerificationService verificationService, MUserRepository mUserRepository) {
        this.mVerificationService = verificationService;
        this.mUserRepository = mUserRepository;
    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(MUser.COLLATION_NAME)) {
            mongoTemplate.createCollection(MUser.COLLATION_NAME);
            IndexOperations indexOpe = mongoTemplate.indexOps(MUser.COLLATION_NAME);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }

    @Override
    public void register(MUser mUser) {
        String code = mUser.getCode();
        boolean exists = mVerificationService.existsCode(code);
        if (exists) {
            mUserRepository.save(mUser);
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
    public void login(MUser mUser) {
        MUser user1 = mUserRepository.findById(mUser.get_id()).get();
        user1.getName().equals(mUser.getName());
        user1.getPassword().equals(mUser.getPassword());
        Properties props =new Properties();
        props.put("_id",mUser.get_id());
        props.put("name",mUser.getName());
        props.put("tel",mUser.getTel());
    }

    @Override
    public void logout(MUser mUser) {

    }
}
