package com.simple.datasourcing.contracts;

import lombok.extern.slf4j.*;

import java.util.*;

@Slf4j
public abstract class DataActions<T> implements DataActionsBase<T> {

    private final DataService<T, ?, ?> service;
    private final History history;

    protected DataActions(DataService<T, ?, ?> service) {
        this.service = service;
        this.history = new History();
    }

    @Override
    public String getTableName() {
        return service.getTableNameBase();
    }

    @Override
    public boolean truncate() {
        return service.truncate(getTableName());
    }

    @Override
    public boolean create(String uniqueId, T data) {
        return service.createBy(uniqueId, data);
    }

    @Override
    public List<T> getAll(String uniqueId) {
        return service.findAllBy(uniqueId, getTableName());
    }

    @Override
    public T getLast(String uniqueId) {
        return service.getLastBy(uniqueId);
    }

    @Override
    public long count(String uniqueId) {
        log.info("Counting [{}] :: [{}]", uniqueId, getTableName());
        var counted = service.countBy(uniqueId, getTableName());
        log.info("Counted :: {}", counted);
        return counted;
    }

    @Override
    public boolean delete(String uniqueId) {
        return service.deleteBy(uniqueId);
    }

    @Override
    public boolean isDeleted(String uniqueId) {
        return service.isDeletedBy(uniqueId);
    }

    public History history() {
        return history;
    }

    public class History implements DataActionsHistory<T> {

        @Override
        public String getTableName() {
            return service.getTableNameHistory();
        }

        @Override
        public boolean truncate() {
            return service.truncate(History.this.getTableName());
        }

        @Override
        public List<T> getAll(String uniqueId) {
            return service.findAllBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public long count(String uniqueId) {
            return service.countBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public boolean historization(String uniqueId) {
            return service.dataHistorization(uniqueId);
        }

        @Override
        public boolean remove(String uniqueId) {
            return service.removeBy(uniqueId, service.getTableNameHistory());
        }
    }
}