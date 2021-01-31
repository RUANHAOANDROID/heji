package com.heji.server.service.impl;

import com.heji.server.data.mongo.Authority;
import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MUser;
import com.heji.server.data.mongo.repository.MUserRepository;
import com.heji.server.exception.NotFindException;
import com.heji.server.security.TokenProvider;
import com.heji.server.service.UserService;
import com.heji.server.service.CodeService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("UserService")
public class UserServiceImpl extends BaseMongoTemplate implements UserService {

    final CodeService mVerificationService;
    final MUserRepository mUserRepository;
    final BCryptPasswordEncoder bCryptPasswordEncoder;

    final AuthenticationManagerBuilder authenticationManagerBuilder;
    final TokenProvider tokenProvider;

    public UserServiceImpl(CodeService verificationService,
                           MUserRepository mUserRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           AuthenticationManagerBuilder authenticationManagerBuilder,
                           TokenProvider jwtTokenProvider) {
        this.mVerificationService = verificationService;
        this.mUserRepository = mUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenProvider = jwtTokenProvider;
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
        boolean exists = mVerificationService.existsCode(code);//邀请码存在
        if (exists) {
            MUser mUser0 = findByTel(mUser.getTel());
            if (null != mUser0) {
                throw new RuntimeException(" 用户已存在");
            }
            String password = bCryptPasswordEncoder.encode(mUser.getPassword());//加密
            mUser.setPassword(password);//存入

            List<Authority> authorities = new ArrayList<>();
            authorities.add(new Authority().setAuthority(Authority.USER));
            authorities.add(new Authority().setAuthority(Authority.ADMIN));
            mUser.setAuthority(authorities);

            MUser newUser = mUserRepository.save(mUser);
            //mVerificationService.deleteCode(code);
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
    public String login(String username, String password) {
        MUser user0 = mUserRepository.findMUserByTel(username);
        boolean success = bCryptPasswordEncoder.matches(password, user0.getPassword());
        UsernamePasswordAuthenticationToken authentication2 = new UsernamePasswordAuthenticationToken(
                username,//电话号码
                password,//没加密的
                user0.getAuthority()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authentication2);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, user0.getAuthority(), true);
        return "Bearer " + jwt;
    }

    @Override
    public User getUserId(String token) {
        User user = (User) tokenProvider.getAuthentication(token).getPrincipal();
        return user;
    }

    @Override
    public void logout(String mUser) {
        SecurityContextHolder.clearContext();
    }

    @Override
    public MUser findByName(String username) {
        Criteria criteria = Criteria.where("name").is(username);
        Query query = new Query(criteria);
        return getMongoTemplate().findOne(query, MUser.class);
    }

    @Override
    public MUser findByTEL(String tel) {
        Criteria criteria = Criteria.where("tel").is(tel);
        Query query = new Query(criteria);
        return getMongoTemplate().findOne(query, MUser.class);
    }

    @Override
    public MUser findByTel(String tel) {
        return mUserRepository.findMUserByTel(tel);
    }
}
