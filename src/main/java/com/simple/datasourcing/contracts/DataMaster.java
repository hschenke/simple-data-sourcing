package com.simple.datasourcing.contracts;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public abstract class DataMaster implements DataMasterActions {

    private final String dbUri;

    protected DataMaster(String dbUri) {
        this.dbUri = dbUri;
    }

    protected abstract DataConnection<?> getDataConnection();

    protected abstract <T> DataService<T, ?, ?> getDataService(Class<T> clazz);
}