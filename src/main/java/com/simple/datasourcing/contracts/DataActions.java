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
    public boolean createFor(String uniqueId, T data) {
        return service.createBy(uniqueId, data);
    }

    @Override
    public List<T> getAllFor(String uniqueId) {
        return service.findAllBy(uniqueId, getTableName());
    }

    @Override
    public T getLastFor(String uniqueId) {
        return service.getLastBy(uniqueId);
    }

    @Override
    public long countFor(String uniqueId) {
        return service.countBy(uniqueId, getTableName());
    }

    @Override
    public boolean deleteFor(String uniqueId) {
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
            return false;
        }

        @Override
        public List<T> getAllFor(String uniqueId) {
            return service.findAllBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public long countFor(String uniqueId) {
            return service.countBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public boolean dataHistorization(String uniqueId) {
            return service.dataHistorization(uniqueId);
        }

        @Override
        public boolean removeFor(String uniqueId) {
            return service.removeBy(uniqueId, service.getTableNameHistory());
        }
    }
}