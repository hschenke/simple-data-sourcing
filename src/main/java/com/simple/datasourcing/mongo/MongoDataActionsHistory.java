package com.simple.datasourcing.mongo;

import com.simple.datasourcing.interfaces.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

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
    public boolean doFullHistory(String uniqueId) {
        try {
            var bulkOpsHistory = getMongo().bulkOps(BulkOperations.BulkMode.ORDERED, this.getTableName());
            bulkOpsHistory.insert(findAllBy(uniqueId, getTableNameBase()));
            bulkOpsHistory.execute();
            var bulkOps = getMongo().bulkOps(BulkOperations.BulkMode.UNORDERED, getTableNameBase());
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
        return getMongo().remove(getQueryById(uniqueId), this.getTableName()).wasAcknowledged();
    }
}