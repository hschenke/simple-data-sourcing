package com.simple.datasourcing.mongo;

import com.simple.datasourcing.interfaces.*;
import lombok.*;
import org.springframework.data.mongodb.core.*;

@Getter
public class MongoDataMaster implements DataMaster {

    private final MongoTemplate mongoDataTemplate;

    public MongoDataMaster(String mongoUri) {
        mongoDataTemplate = new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoUri));
    }

    @Override
    public <T> MongoDataActions<T> getDataActions(Class<T> clazz) {
        return new MongoDataActions<>(mongoDataTemplate, clazz);
    }

    @Override
    public <T> MongoDataActionsHistory<T> getDataActionsHistory(Class<T> clazz) {
        return new MongoDataActionsHistory<>(getDataActions(clazz));
    }
}