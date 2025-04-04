package com.simple.datasourcing.contracts;

import com.simple.datasourcing.model.*;

import java.util.*;

public abstract class DataActionsBase<T, DT, Q> {

    private final DT dataTemplate;
    private final Class<T> clazz;

    public DataActionsBase(DT dataTemplate, Class<T> clazz) {
        this.dataTemplate = dataTemplate;
        this.clazz = clazz;
        createTables(dataTemplate, clazz);
    }

    protected DT getDataTemplate() {
        return dataTemplate;
    }

    protected abstract void createTables(DT dataTemplate, Class<T> clazz);

    protected abstract Q getQueryById(String uniqueId);

    protected abstract Q getQueryLastById(String uniqueId);

    protected abstract boolean truncate(String tableName);

    protected abstract List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName);

    protected abstract long countBy(String uniqueId, String tableName);

    protected abstract DataEvent<T> findLastBy(String uniqueId);

    protected abstract DataEvent<T> insertBy(DataEvent<T> dataEvent);

    protected List<T> findAllBy(String uniqueId, String tableName) {
        return findAllEventsBy(uniqueId, tableName).stream().map(DataEvent::getData).toList();
    }

    protected String getTableNameBase() {
        return clazz.getSimpleName();
    }

    protected String getTableNameHistory() {
        return getTableNameBase() + "-history";
    }
}