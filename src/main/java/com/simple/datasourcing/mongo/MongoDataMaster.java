package com.simple.datasourcing.mongo;

import com.simple.datasourcing.interfaces.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

public class MongoDataMaster implements DataMaster<MongoTemplate> {

    private final MongoTemplate mongoDataTemplate;

    public MongoDataMaster(String mongoUri) {
        mongoDataTemplate = new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoUri));
    }

    @Override
    public MongoTemplate getDataTemplate() {
        return mongoDataTemplate;
    }

    @Override
    public <T> MongoDataActions<T> getDataActions(Class<T> clazz) {
        return new MongoDataActions<>(mongoDataTemplate, clazz);
    }

    @Override
    public <T> MongoDataActionsHistory<T> getDataActionsHistory(Class<T> clazz) {
        return new MongoDataActionsHistory<>(mongoDataTemplate, clazz);
    }

    @Override
    public <T> void initActionsFor(List<Class<T>> classes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}