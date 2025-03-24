package com.simple.datasourcing.mongo;

import com.simple.datasourcing.model.*;
import com.simple.datasourcing.interfaces.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Slf4j
@Getter
public class MongoDataActionsHistory<T> implements DataActionsHistory<T> {

    private final MongoDataActions<T> dataActions;
    private final MongoTemplate mongo;
    private final String tableName;

    public MongoDataActionsHistory(MongoDataActions<T> dataActions) {
        this.dataActions = dataActions;
        this.mongo = dataActions.getMongo();
        this.tableName = getTableNameHistory(dataActions.getTableName());
        mongo.createCollection(tableName);
    }

    @Override
    public boolean doFullHistory(String uniqueId) {
        try {
            var bulkOpsHistory = mongo.bulkOps(BulkOperations.BulkMode.ORDERED, tableName);
            bulkOpsHistory.insert(dataActions.findAllBy(uniqueId));
            bulkOpsHistory.execute();
            var bulkOps = mongo.bulkOps(BulkOperations.BulkMode.UNORDERED, dataActions.getTableName());
            bulkOps.remove(getQueryById(uniqueId));
            bulkOps.execute();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeFor(String uniqueId) {
        return mongo.remove(getQueryById(uniqueId), tableName).wasAcknowledged();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DataEvent<T>> findAllBy(String uniqueId) {
        return (List<DataEvent<T>>) mongo.find(getQueryById(uniqueId), DataEvent.create().getClass(), tableName);
    }

    @Override
    public long countBy(String uniqueId) {
        return mongo.count(getQueryById(uniqueId), tableName);
    }
}