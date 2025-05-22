package com.simple.datasourcing.contracts.reactive;

import lombok.*;
import lombok.extern.slf4j.*;
import reactor.core.publisher.*;

@Slf4j
public abstract class ReactiveDataActions<T> implements ReactiveDataActionsBase<T> {

    @Getter
    private final ReactiveDataService<T, ?, ?> service;
    private final History history;

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
    public Mono<Boolean> createFor(String uniqueId, T data) {
        return service.createBy(uniqueId, data);
    }

    @Override
    public Flux<T> getAllFor(String uniqueId) {
        return service.findAllBy(uniqueId, getTableName());
    }

    @Override
    public Mono<T> getLastFor(String uniqueId) {
        return service.getLastBy(uniqueId);
    }

    @Override
    public Mono<Long> countFor(String uniqueId) {
        return service.countBy(uniqueId, getTableName());
    }

    @Override
    public Mono<Boolean> deleteFor(String uniqueId) {
        return service.deleteBy(uniqueId);
    }

    @Override
    public Mono<Boolean> isDeleted(String uniqueId) {
        return service.isDeletedBy(uniqueId);
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
        public Flux<T> getAllFor(String uniqueId) {
            return service.findAllBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public Mono<Long> countFor(String uniqueId) {
            return service.countBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public Mono<Boolean> dataHistorization(String uniqueId) {
            return service.dataHistorization(uniqueId);
        }

        @Override
        public Mono<Boolean> removeFor(String uniqueId) {
            return service.removeBy(uniqueId, service.getTableNameHistory()).hasElement();
        }
    }
}