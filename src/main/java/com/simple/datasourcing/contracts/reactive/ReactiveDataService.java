package com.simple.datasourcing.contracts.reactive;

import com.simple.datasourcing.contracts.*;
import com.simple.datasourcing.error.*;
import com.simple.datasourcing.model.*;
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

    public Flux<T> findAllBy(String uniqueId, String tableName) {
        log.info("Find all by id :: [{}] :: table :: [{}]", uniqueId, tableName);
        return findAllEventsBy(uniqueId, tableName)
                .map(DataEvent::getData);
    }

    public Mono<Boolean> createBy(String uniqueId, T data) {
        return insertBy(DataEvent.<T>create().setData(uniqueId, Boolean.FALSE, data))
                .doOnSuccess(de -> log.info("Inserted data :: {}", de))
                .doOnError(error -> log.error("Error :: not inserted :: {}", error.getMessage()))
                .hasElement();
    }

    public Mono<T> getLastBy(String uniqueId) {
        log.info("Get last by id :: [{}]", uniqueId);
        return findLastBy(uniqueId)
                .map(DataEvent::getData);

    }

    public Mono<Boolean> deleteBy(String uniqueId) {
        log.info("Delete base by id :: [{}]", uniqueId);
        return dataHistorization(uniqueId)
                .flatMap(historized -> {
                    if (historized)
                        return insertBy(DataEvent.<T>create().setData(uniqueId, Boolean.TRUE, null)).hasElement();
                    else
                        return Mono.just(false);
                });
    }

    public Mono<Boolean> isDeletedBy(String uniqueId) {
        log.info("Check deletion by :: [{}]", uniqueId);
        return findLastBy(uniqueId)
                .map(DataEvent::getDeleted);
    }
}