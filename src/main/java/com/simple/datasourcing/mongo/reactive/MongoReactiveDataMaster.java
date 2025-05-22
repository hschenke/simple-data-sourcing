package com.simple.datasourcing.mongo.reactive;

import com.simple.datasourcing.contracts.reactive.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public class MongoReactiveDataMaster extends ReactiveDataMaster {

    public MongoReactiveDataMaster(String dbUri) {
        super(dbUri);
    }

    @Override
    protected MongoReactiveDataConnection generateDataConnection() {
        return new MongoReactiveDataConnection(getDbUri());
    }

    @Override
    protected <T> MongoReactiveDataService<T> getDataService(Class<T> clazz) {
        return new MongoReactiveDataService<>(generateDataConnection(), clazz);
    }

    @Override
    public <T> MongoReactiveDataActions<T> getDataActions(Class<T> clazz) {
        return new MongoReactiveDataActions<>(getDataService(clazz));
    }
}