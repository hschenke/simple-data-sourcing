package com.simple.datasourcing.contracts.actions;

import com.simple.datasourcing.contracts.service.*;
import com.simple.datasourcing.model.*;
import com.simple.datasourcing.thread.*;
import lombok.extern.slf4j.*;

import java.util.*;
import java.util.function.*;

@Slf4j
public class DataActions<T> implements DataActionsBase<T> {

    private final DataService<T, ?, ?> service;
    private final History history;
    public boolean onDeleteDoDataHistorization = true;

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
        log.info("Truncating table {}", getTableName());
        return service.truncate(getTableName());
    }

    @Override
    public boolean create(String uniqueId, T data) {
        log.info("Insert data :: [{}] - {}", uniqueId, data);
        return service.insertBy(DataEvent.<T>create().setDataset(uniqueId, Boolean.FALSE, data));
    }

    @Override
    public List<DataEvent<T>> getAll() {
        log.info("Find all by table :: [{}]", getTableName());
        return service.findAll(getTableName());
    }

    @Override
    public List<T> getAll(String uniqueId) {
        return findAllBy(uniqueId, getTableName());
    }

    @Override
    public List<String> getAllIds() {
        return getAll().stream().map(DataEvent::getUniqueId).toList();
    }

    @Override
    public T getLast(String uniqueId) {
        log.info("Get last by id :: [{}]", uniqueId);
        return Optional.ofNullable(service.findLastBy(uniqueId))
                .map(DataEvent::getData)
                .orElse(null);
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
        log.info("Delete base by id :: [{}]", uniqueId);
        if (onDeleteDoDataHistorization) dataHistorization(uniqueId);
        return service.insertBy(DataEvent.<T>create().setDataset(uniqueId, Boolean.TRUE, null));
    }

    @Override
    public ThreadMaster deleteInBackground(String uniqueId) {
        return ThreadMaster.action(() -> delete(uniqueId)).execute();
    }

    @Override
    public ThreadMaster deleteInBackgroundCallback(String uniqueId, Consumer<Boolean> callback, Consumer<Exception> errorCallback) {
        return ThreadMaster
                .action(() -> delete(uniqueId))
                .callback(callback)
                .onError(errorCallback)
                .execute();
    }

    @Override
    public boolean isDeleted(String uniqueId) {
        log.info("Check deletion by :: [{}]", uniqueId);
        return Optional.ofNullable(service.findLastBy(uniqueId))
                .map(DataEvent::getDeleted)
                .orElse(Boolean.FALSE);
    }


    protected List<T> findAllBy(String uniqueId, String tableName) {
        log.info("Find all by id :: [{}] :: table :: [{}]", uniqueId, tableName);
        return service.findAllEventsBy(uniqueId, tableName).stream().map(DataEvent::getData).toList();
    }

    protected boolean dataHistorization(String uniqueId) {
        log.info("Data Historization of [{}]", uniqueId);
        try {
            log.info("First :: move to history...");
            var moveToHistory = service.moveToHistory(uniqueId);
            log.info("Success :: [{}]", moveToHistory);
            log.info("Second :: remove from base...");
            var removeFromBase = service.removeFromBase(uniqueId);
            log.info("Success :: [{}]", removeFromBase);
            return moveToHistory && removeFromBase;
        } catch (Exception e) {
            log.error("Data Historization error :: {}", e.getMessage());
            return false;
        }
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
        public List<DataEvent<T>> getAll() {
            return service.findAll(getTableName());
        }

        @Override
        public List<T> getAll(String uniqueId) {
            return findAllBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public List<String> getAllIds() {
            return getAll().stream().map(DataEvent::getUniqueId).toList();
        }

        @Override
        public long count(String uniqueId) {
            return service.countBy(uniqueId, service.getTableNameHistory());
        }

        @Override
        public boolean historization(String uniqueId) {
            return dataHistorization(uniqueId);
        }

        @Override
        public ThreadMaster historizationInBackground(String uniqueId) {
            return ThreadMaster.action(() -> historization(uniqueId)).execute();
        }

        @Override
        public ThreadMaster historizationInBackgroundCallback(String uniqueId, Consumer<Boolean> callback, Consumer<Exception> errorCallback) {
            return ThreadMaster
                    .action(() -> historization(uniqueId))
                    .callback(callback)
                    .onError(errorCallback)
                    .execute();
        }

        @Override
        public boolean remove(String uniqueId) {
            log.info("Removing [{}] from [{}]", uniqueId, getTableName());
            return service.removeBy(uniqueId, getTableName());
        }

        @Override
        public ThreadMaster removeInBackground(String uniqueId) {
            return ThreadMaster.action(() -> remove(uniqueId)).execute();
        }

        @Override
        public ThreadMaster removeInBackgroundCallback(String uniqueId, Consumer<Boolean> callback, Consumer<Exception> errorCallback) {
            return ThreadMaster
                    .action(() -> remove(uniqueId))
                    .callback(callback)
                    .onError(errorCallback)
                    .execute();
        }
    }
}