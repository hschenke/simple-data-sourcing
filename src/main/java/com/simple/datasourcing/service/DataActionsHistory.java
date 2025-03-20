package com.simple.datasourcing.service;

import com.mongodb.client.result.*;
import com.simple.datasourcing.model.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Slf4j
public class DataActionsHistory<T> extends DataActions<T> {

    private final String tableNameHistory;

    public DataActionsHistory(DataCon dataCon, Class<T> clazz) {
        super(dataCon, clazz);
        this.tableNameHistory = clazz.getSimpleName() + "-history";
        super.dataCon.getTemplate().createCollection(tableNameHistory);
    }

    public DataActionsHistory(String mongoUri, Class<T> clazz) {
        this(new DataCon(mongoUri), clazz);
    }

    public boolean doFullHistory(String uniqueId) {
        try {
            var bulkOpsHistory = dataCon.getTemplate().bulkOps(BulkOperations.BulkMode.ORDERED, tableNameHistory);
            bulkOpsHistory.insert(findById(uniqueId));
            bulkOpsHistory.execute();
            var bulkOps = dataCon.getTemplate().bulkOps(BulkOperations.BulkMode.UNORDERED, super.getTableName());
            bulkOps.remove(getQueryById(uniqueId));
            bulkOps.execute();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public DeleteResult removeFor(String uniqueId) {
        return dataCon.getTemplate().remove(getQueryById(uniqueId), tableNameHistory);
    }

    @Override
    public List<T> getAllFor(String uniqueId) {
        return findById(uniqueId, tableNameHistory).stream().map(DataEvent::getData).toList();
    }

    @Override
    public long countFor(String uniqueId) {
        return dataCon.getTemplate().count(getQueryById(uniqueId), tableNameHistory);
    }

    @Override
    public String getTableName() {
        return tableNameHistory;
    }

    @Override
    public T getLastFor(String uniqueId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataEvent<T> createFor(String uniqueId, T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataEvent<T> deleteFor(String uniqueId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDeleted(String uniqueId) {
        throw new UnsupportedOperationException();
    }
}