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

    public class MongoActions<T> extends Actions<T, MongoDataService<T>> {

        public MongoActions(MongoTemplate dbTemplate, Class<T> clazz) {
            super(dbTemplate, clazz);
        }

        @Override
        protected MongoDataService<T> generateDataActions(MongoTemplate dbTemplate, Class<T> clazz) {
            return new MongoDataService<>(clazz, dbTemplate);
        }

        @Override
        public MongoDataActions<T> getBase() {
            return new MongoDataActions<>(getDataActions());
        }

        @Override
        public MongoDataActions<T>.History getHistory() {
            return getBase().new History();
        }

        @Override
        public AllActions<T> getAll() {
            return new AllActions<>(getBase(), getHistory());
        }
    }
}