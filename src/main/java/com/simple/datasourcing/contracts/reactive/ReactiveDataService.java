package com.simple.datasourcing.contracts.reactive;

import com.simple.datasourcing.contracts.connection.*;
import com.simple.datasourcing.error.*;
import lombok.*;
import lombok.extern.slf4j.*;
import reactor.core.publisher.*;

@Slf4j
@Getter
public abstract class ReactiveDataService<T, DT, Q> implements ReactiveDataServiceActions<T, Q> {

    private final DataConnection<DT> dataConnection;
    private final Class<T> clazz;
    private final String tableNameBase;
    private final String tableNameHistory;
    private final Mono<Boolean> cachedInitializeTables;

    protected ReactiveDataService(DataConnection<DT> dataConnection, Class<T> clazz) {
        this.dataConnection = dataConnection;
        this.clazz = clazz;
        this.tableNameBase = clazz.getSimpleName().toLowerCase();
        this.tableNameHistory = tableNameBase + "_history";
        this.cachedInitializeTables = Mono.defer(() ->
                tableExistsCheck(tableNameBase)
                        .flatMap(_ -> createBaseTable())
                        .then(tableExistsCheck(tableNameHistory))
                        .flatMap(_ -> createHistoryTable())
                        .then(Mono.just(true))
                        .onErrorMap(error -> { // Use onErrorMap to transform/wrap errors in the pipeline
                            log.error("Error during table creation setup: {}", error.getMessage(), error);
                            return new TableNotCreatedException("Failed to initialize tables");
                        })).cache(); // cache here important for reuse
    }

    private Mono<Boolean> tableExistsCheck(String tableName) {
        return tableExists(tableName)
                .filter(exists -> !exists) // if collection not exists, do creation on overnext step
                .switchIfEmpty(Mono.defer(Mono::empty)); // here pipe runs out if collection exists
    }

    public DT uncheckedDataTemplate() {
        return dataConnection.getDataTemplate();
    }

    public Mono<DT> checkedDataTemplate() {
        return cachedInitializeTables.then(Mono.just(uncheckedDataTemplate()));
    }
}