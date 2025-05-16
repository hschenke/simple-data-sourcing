package com.simple.datasourcing.contracts;

import com.simple.datasourcing.model.*;
import lombok.*;
import lombok.extern.slf4j.*;

import java.util.*;

@Slf4j
@Getter
public abstract class DataService<T, DT, Q> implements DataServiceActions<T, Q> {

    private final DataConnection<DT> dataConnection;
    private final Class<T> clazz;
    private final String tableNameBase;
    private final String tableNameHistory;

    protected DataService(DataConnection<DT> dataConnection, Class<T> clazz) {
        this.dataConnection = dataConnection;
        this.clazz = clazz;
        this.tableNameBase = clazz.getSimpleName().toLowerCase();
        this.tableNameHistory = tableNameBase + "_history";
        if (!bothTablesExists()) {
            log.info("Creating tables :: {} and {}", tableNameBase, tableNameHistory);
            createTables();
            if (!bothTablesExists()) throw new TablesNotExistException();
        }
    }

    public DT dataTemplate() {
        return dataConnection.getDataTemplate();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean bothTablesExists() {
        return tableExists(tableNameBase) && tableExists(tableNameHistory);
    }

    public List<T> findAllBy(String uniqueId, String tableName) {
        log.info("Find all by id :: [{}] :: table :: [{}]", uniqueId, tableName);
        return findAllEventsBy(uniqueId, tableName).stream().map(DataEvent::getData).toList();
    }

    public boolean createBy(String uniqueId, T data) {
        log.info("Insert data :: [{}] - {}", uniqueId, data);
        return insertBy(DataEvent.<T>create().setData(uniqueId, Boolean.FALSE, data));
    }

    public T getLastBy(String uniqueId) {
        log.info("Get last by id :: [{}]", uniqueId);
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::getData)
                .orElse(null);
    }

    public boolean deleteBy(String uniqueId) {
        log.info("Delete base by id :: [{}]", uniqueId);
        if(dataHistorization(uniqueId))
            return insertBy(DataEvent.<T>create().setData(uniqueId, Boolean.TRUE, null));
        return false;
    }

    public boolean isDeletedBy(String uniqueId) {
        log.info("Check deletion by :: [{}]", uniqueId);
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::getDeleted)
                .orElse(Boolean.FALSE);
    }
}