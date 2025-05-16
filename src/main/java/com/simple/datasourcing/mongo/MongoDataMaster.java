package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public class MongoDataMaster extends DataMaster {

    public MongoDataMaster(String dbUri) {
        super(dbUri);
    }

    @Override
    protected MongoDataConnection generateDataConnection() {
        return new MongoDataConnection(getDbUri());
    }

    @Override
    protected <T> MongoDataService<T> getDataService(Class<T> clazz) {
        return new MongoDataService<>(generateDataConnection(), clazz);
    }

    @Override
    public <T> MongoDataActions<T> getDataActions(Class<T> clazz) {
        return new MongoDataActions<>(getDataService(clazz));
    }
}