package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import org.springframework.data.mongodb.core.*;

public class MongoDataMaster extends DataMaster<MongoTemplate> {

    private MongoDataMaster(String mongoUri) {
        super(mongoUri);
    }

    @Override
    protected MongoTemplate generateDbTemplate(String dbUri) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(dbUri));
    }

    public static MongoDataMaster get(String mongoUri) {
        return new MongoDataMaster(mongoUri);
    }

    @Override
    public <T> MongoActions<T> actionsFor(Class<T> clazz) {
        return new MongoActions<>(getDbTemplate(), clazz);
    }

    public class MongoActions<T> extends Actions<T, MongoDataActions<T>> {

        public MongoActions(MongoTemplate dbTemplate, Class<T> clazz) {
            super(dbTemplate, clazz);
        }

        @Override
        protected MongoDataActions<T> generateDataActions(MongoTemplate dbTemplate, Class<T> clazz) {
            return new MongoDataActions<>(dbTemplate, clazz);
        }

        @Override
        public MongoDataActionsBase<T> getBase() {
            return new MongoDataActionsBase<>(getDataActions());
        }

        @Override
        public MongoDataActionsHistory<T> getHistory() {
            return new MongoDataActionsHistory<>(getDataActions());
        }
    }
}