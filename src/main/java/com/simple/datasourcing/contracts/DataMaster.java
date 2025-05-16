package com.simple.datasourcing.contracts;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public abstract class DataMaster implements DataMasterActions {

    private final String dbUri;
    private final DataConnection<?> dataConn;

    protected DataMaster(String dbUri) {
        this.dbUri = dbUri;
        this.dataConn = generateDataConnection();
    }

    protected abstract DataConnection<?> generateDataConnection();

    protected abstract <T> DataService<T, ?, ?> getDataService(Class<T> clazz);
}