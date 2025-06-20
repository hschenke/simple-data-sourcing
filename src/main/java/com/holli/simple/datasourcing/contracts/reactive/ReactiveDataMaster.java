package com.holli.simple.datasourcing.contracts.reactive;

import com.holli.simple.datasourcing.contracts.connection.*;
import lombok.*;
import lombok.extern.slf4j.*;
import reactor.core.publisher.*;

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

    @Override
    public Mono<Void> deleteAll(String uniqueId, Flux<ReactiveDataActions<?>> dataActions) {
        return dataActions.flatMap(actions -> actions.delete(uniqueId)).then();
    }
}