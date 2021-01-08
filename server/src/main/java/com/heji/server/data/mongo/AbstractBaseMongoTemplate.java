package com.heji.server.data.mongo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public abstract class AbstractBaseMongoTemplate implements ApplicationContextAware {
    private MongoTemplate mongoTemplate;
    private GridFsTemplate gridFsTemplate;

    protected void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    protected GridFsTemplate getGridFsTemplate() {
        return gridFsTemplate;
    }

    protected void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    protected abstract void init();

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        MongoTemplate mongoTemplate = appContext.getBean("mongoTemplate", MongoTemplate.class);
        GridFsTemplate gridFsTemplate = appContext.getBean("gridFsTemplate", GridFsTemplate.class);
        setMongoTemplate(mongoTemplate);
        setGridFsTemplate(gridFsTemplate);
        init();
    }
}
