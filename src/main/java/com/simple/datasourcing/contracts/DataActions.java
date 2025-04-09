package com.simple.datasourcing.contracts;

import com.simple.datasourcing.model.*;
import lombok.*;
import lombok.extern.slf4j.*;

import java.util.*;

@Slf4j
@Getter
public abstract class DataActions<T, DT, Q> {

    private final DT dataTemplate;
    private final Class<T> clazz;
    private final String tableNameBase;
    private final String tableNameHistory;

    protected DataActions(DT dataTemplate, Class<T> clazz) {
        this.dataTemplate = dataTemplate;
        this.clazz = clazz;
        this.tableNameBase = clazz.getSimpleName();
        this.tableNameHistory = tableNameBase + "-history";
        if (!bothTablesExists()) {
            log.info("Creating tables :: {} and {}", tableNameBase, tableNameHistory);
            createTables();
            if (!bothTablesExists()) throw new TablesNotExistException();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean bothTablesExists() {
        return tableExists(tableNameBase) && tableExists(tableNameHistory);
    }

    protected abstract void createTables();

    protected abstract Q getQueryById(String uniqueId);

    protected abstract Q getQueryLastById(String uniqueId);

    protected abstract boolean truncate(String tableName);

    protected abstract List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName);

    protected abstract boolean tableExists(String tableName);

    protected abstract long countBy(String uniqueId, String tableName);

    protected abstract DataEvent<T> findLastBy(String uniqueId);

    protected abstract DataEvent<T> insertBy(DataEvent<T> dataEvent);

    protected DataEvent<T> createBy(String uniqueId, T data) {
        log.info("Insert data :: [{}] - {}", uniqueId, data);
        return insertBy(DataEvent.<T>create().setData(uniqueId, false, data));
    }

    protected T getLastBy(String uniqueId) {
        log.info("Get last by id :: [{}]", uniqueId);
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::getData)
                .orElse(null);
    }

    protected DataEvent<T> deleteBy(String uniqueId) {
        log.info("Delete base by id :: [{}]", uniqueId);
        return insertBy(DataEvent.<T>create().setData(uniqueId, true, null));
    }

    protected long countBaseBy(String uniqueId) {
        log.info("Count base by id :: [{}]", uniqueId);
        return isDeletedBy(uniqueId) ? 0 : countBy(uniqueId, getTableNameBase());
    }

    protected List<T> getAllBaseBy(String uniqueId) {
        log.info("Get all base by id :: [{}]", uniqueId);
        return isDeletedBy(uniqueId) ? List.of() : findAllBy(uniqueId, getTableNameBase());
    }

    protected List<T> findAllBy(String uniqueId, String tableName) {
        log.info("Find all by id :: [{}] :: table :: [{}]", uniqueId, tableName);
        return findAllEventsBy(uniqueId, tableName).stream().map(DataEvent::getData).toList();
    }

    protected boolean isDeletedBy(String uniqueId) {
        log.info("Check deletion by :: [{}]", uniqueId);
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::isDeleted)
                .orElse(false);
    }
}