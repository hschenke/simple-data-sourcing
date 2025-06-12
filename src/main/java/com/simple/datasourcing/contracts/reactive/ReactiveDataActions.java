package com.simple.datasourcing.contracts.reactive;

import com.simple.datasourcing.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import reactor.core.publisher.*;

@Slf4j
public class ReactiveDataActions<T> implements ReactiveDataActionsBase<T> {

    @Getter
    private final ReactiveDataService<T, ?, ?> service;
    private final History history;
    public boolean onDeleteDoDataHistorization = true;

    protected ReactiveDataActions(ReactiveDataService<T, ?, ?> service) {
        this.service = service;
        this.history = new History();
    }

    @Override
    public String getTableName() {
        return service.getTableNameBase();
    }

    @Override
    public Mono<Boolean> truncate() {
        return service.truncate(getTableName());
    }

    @Override
    public Mono<Boolean> add(String uniqueId, T data) {
        return service.insertBy(DataEvent.<T>create().setDataset(uniqueId, Boolean.FALSE, data))
                .doOnSuccess(de -> log.info("Inserted data :: {}", de))
                .doOnError(error -> log.error("Error :: not inserted :: {}", error.getMessage()))
                .hasElement();
    }

    @Override
    public Flux<DataEvent<T>> getAll() {
        return service.findAll(getTableName());
    }

    @Override
    public Flux<T> getAll(String uniqueId) {
        return findAllBy(uniqueId, getTableName());
    }

    @Override
    public Flux<String> getAllIds(String uniqueId) {
        return getAll().map(DataEvent::getUniqueId);
    }

    @Override
    public Mono<T> getLast(String uniqueId) {
        log.info("Get last by id :: [{}]", uniqueId);
        return service.findLastBy(uniqueId)
                .map(DataEvent::getData)
                .onErrorResume(_ -> Mono.empty());
    }

    @Override
    public Mono<Long> count(String uniqueId) {
        return service.countBy(uniqueId, getTableName());
    }

    @Override
    public Mono<Boolean> delete(String uniqueId) {
        log.info("Delete base by id :: [{}]", uniqueId);
        return onDeleteDoDataHistorization ?
                dataHistorization(uniqueId)
                        .flatMap(historized -> {
                            if (historized) return deleteBy(uniqueId);
                            else return Mono.just(false);
                        })
                :
                deleteBy(uniqueId);
    }

    private Mono<Boolean> deleteBy(String uniqueId) {
        return service.insertBy(DataEvent.<T>create().setDataset(uniqueId, Boolean.TRUE, null)).hasElement();
    }

    @Override
    public Mono<Boolean> isDeleted(String uniqueId) {
        log.info("Check deletion by :: [{}]", uniqueId);
        return service.findLastBy(uniqueId)
                .map(DataEvent::getDeleted);
    }

    public Flux<T> findAllBy(String uniqueId, String tableName) {
        log.info("Find all by id :: [{}] :: table :: [{}]", uniqueId, tableName);
        return service.findAllEventsBy(uniqueId, tableName)
                .map(DataEvent::getData);
    }

    public Mono<Boolean> dataHistorization(String uniqueId) {
        log.info("Data historization :: [{}]", uniqueId);
        return service.moveToHistory(uniqueId)
                .flatMap(_ -> service.removeFromBase(uniqueId))
                .doOnSuccess(_ -> log.info("Historization completed successfully"))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorResume(_ -> Mono.just(false));
    }

    public History history() {
        return history;
    }

    public class History implements ReactiveDataActionsHistory<T> {

        @Override
        public String getTableName() {
            return service.getTableNameHistory();
        }

        @Override
        public Mono<Boolean> truncate() {
            return service.truncate(History.this.getTableName());
        }

        @Override
        public Flux<DataEvent<T>> getAll() {
            return service.findAll(getTableName());
        }

        @Override
        public Flux<T> getAll(String uniqueId) {
            return findAllBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public Flux<String> getAllIds(String uniqueId) {
            return getAll().map(DataEvent::getUniqueId);
        }

        @Override
        public Mono<Long> count(String uniqueId) {
            return service.countBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public Mono<Boolean> historization(String uniqueId) {
            return dataHistorization(uniqueId);
        }

        @Override
        public Mono<Boolean> remove(String uniqueId) {
            return service.removeBy(uniqueId, service.getTableNameHistory()).hasElement();
        }
    }
}