package com.simple.datasourcing.service;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

@Getter
@Slf4j
public class DataActionsHistory<T> extends DataActions<T> {

    private final String tableNameHistory;

    public DataActionsHistory(String mongoUri, Class<T> clazz) {
        super(mongoUri, clazz);
        this.tableNameHistory = clazz.getSimpleName() + "-history";
        super.template.createCollection(tableNameHistory);
    }

    public boolean doFullHistory(String uniqueId) {
        try {
            var bulkOpsHistory = template.bulkOps(BulkOperations.BulkMode.ORDERED, tableNameHistory);
            bulkOpsHistory.insert(findById(uniqueId));
            bulkOpsHistory.execute();
            var bulkOps = template.bulkOps(BulkOperations.BulkMode.UNORDERED, getTableName());
            bulkOps.remove(getQueryById(uniqueId));
            bulkOps.execute();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}