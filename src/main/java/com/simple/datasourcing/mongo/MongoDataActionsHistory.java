package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Slf4j
@Getter
public class MongoDataActionsHistory<T> extends MongoDataActionsBase<T> implements DataActionsHistory<T> {

    public MongoDataActionsHistory(MongoTemplate mongo, Class<T> clazz) {
        super(mongo, clazz);
    }

    @Override
    public String getTableName() {
        return getTableNameHistory();
    }

    @Override
    public boolean truncate() {
        return false;
    }

    @Override
    public List<T> getAllFor(String uniqueId) {
        return findAllBy(uniqueId, getTableNameHistory());
    }

    @Override
    public long countFor(String uniqueId) {
        return countBy(uniqueId, getTableNameHistory());
    }

    @Override
    public boolean doFullHistory(String uniqueId) {
        try {
            var bulkOpsHistory = getDataTemplate().bulkOps(BulkOperations.BulkMode.ORDERED, getTableNameHistory());
            bulkOpsHistory.insert(findAllEventsBy(uniqueId, getTableNameBase()));
            bulkOpsHistory.execute();
            var bulkOps = getDataTemplate().bulkOps(BulkOperations.BulkMode.UNORDERED, getTableNameBase());
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
        return getDataTemplate().remove(getQueryById(uniqueId), getTableNameHistory()).wasAcknowledged();
    }
}