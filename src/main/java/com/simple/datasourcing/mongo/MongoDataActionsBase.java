package com.simple.datasourcing.mongo;

import com.simple.datasourcing.interfaces.*;
import com.simple.datasourcing.model.*;
import lombok.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Getter
public abstract class MongoDataActionsBase<T> implements DataActionsBase<T> {

    private final MongoTemplate mongo;
    private final String tableNameBase;
    private final String tableNameHistory;

    public MongoDataActionsBase(MongoTemplate mongo, Class<T> clazz) {
        this.mongo = mongo;
        this.tableNameBase = getTableNameBase(clazz);
        this.tableNameHistory = getTableNameHistory(clazz);
        mongo.createCollection(tableNameBase);
        mongo.createCollection(tableNameHistory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DataEvent<T>> findAllBy(String uniqueId, String tableName) {
        return (List<DataEvent<T>>) mongo.find(getQueryById(uniqueId), DataEvent.create().getClass(), tableName);
    }

    @Override
    public long countBy(String uniqueId, String tableName) {
        return mongo.count(getQueryById(uniqueId), tableName);
    }
}