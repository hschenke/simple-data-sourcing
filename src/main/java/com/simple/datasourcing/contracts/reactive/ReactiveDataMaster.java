package com.simple.datasourcing.contracts.reactive;

import com.simple.datasourcing.contracts.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public abstract class ReactiveDataMaster implements ReactiveDataMasterActions {

    private final String dbUri;
    private final DataConnection<?> dataConn;

    protected ReactiveDataMaster(String dbUri) {
        this.dbUri = dbUri;
        this.dataConn = generateDataConnection();
    }

    protected abstract DataConnection<?> generateDataConnection();

    protected abstract <T> ReactiveDataService<T, ?, ?> getDataService(Class<T> clazz);
}