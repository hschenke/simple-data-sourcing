package com.holli.simple.datasourcing.mongo.reactive;

import com.holli.simple.datasourcing.contracts.reactive.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public class ReactiveMongoDataMaster extends ReactiveDataMaster {

    public ReactiveMongoDataMaster(String dbUri) {
        super(dbUri);
    }

    @Override
    protected ReactiveMongoDataConnection generateDataConnection() {
        return new ReactiveMongoDataConnection(getDbUri());
    }

    @Override
    protected <T> ReactiveMongoDataService<T> getDataService(Class<T> clazz) {
        return new ReactiveMongoDataService<>(generateDataConnection(), clazz);
    }

    @Override
    public <T> ReactiveMongoDataActions<T> getDataActions(Class<T> clazz) {
        return new ReactiveMongoDataActions<>(getDataService(clazz));
    }
}