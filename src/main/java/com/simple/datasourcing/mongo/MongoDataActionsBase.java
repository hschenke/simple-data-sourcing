package com.simple.datasourcing.mongo;

import com.simple.datasourcing.interfaces.*;
import com.simple.datasourcing.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

@Getter
@Slf4j
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
    public boolean truncate(String tableName) {
        log.info("Truncating table {}", tableName);
        return mongo.remove(new Query(), tableName).wasAcknowledged();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DataEvent<T>> findAllBy(String uniqueId, String tableName) {
        log.info("Finding all in {} by uniqueId {}", tableName, uniqueId);
        return (List<DataEvent<T>>) mongo.find(getQueryById(uniqueId), DataEvent.create().getClass(), tableName);
    }

    @Override
    public long countBy(String uniqueId, String tableName) {
        log.info("Counting {} by uniqueId {}", tableName, uniqueId);
        return mongo.count(getQueryById(uniqueId), tableName);
    }
}