package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import com.simple.datasourcing.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Getter
@Slf4j
public class MongoDataActions<T> implements DataActionsBase<T> {

    private final MongoDataService<T> actions;

    public MongoDataActions(MongoDataService<T> actions) {
        this.actions = actions;
    }

    public class History implements DataActionsHistory<T> {

        @Override
        public String getTableName() {
            return actions.getTableNameHistory();
        }

        @Override
        public boolean truncate() {
            return false;
        }

        @Override
        public List<T> getAllFor(String uniqueId) {
            return actions.findAllBy(uniqueId, actions.getTableNameHistory());
        }

        @Override
        public long countFor(String uniqueId) {
            return actions.countBy(uniqueId, actions.getTableNameHistory());
        }

        @Override
        public boolean doFullHistory(String uniqueId) {
            try {
                var bulkOpsHistory = actions.getDataTemplate().bulkOps(BulkOperations.BulkMode.ORDERED, actions.getTableNameHistory());
                bulkOpsHistory.insert(actions.findAllEventsBy(uniqueId, actions.getTableNameBase()));
                bulkOpsHistory.execute();
                var bulkOps = actions.getDataTemplate().bulkOps(BulkOperations.BulkMode.UNORDERED, actions.getTableNameBase());
                bulkOps.remove(actions.getQueryById(uniqueId));
                bulkOps.execute();
                return true;
            } catch (Exception e) {
                log.error(e.getMessage());
                return false;
            }
        }

        @Override
        public boolean removeFor(String uniqueId) {
            return actions.getDataTemplate().remove(actions.getQueryById(uniqueId), actions.getTableNameHistory()).wasAcknowledged();
        }
    }

    @Override
    public String getTableName() {
        return actions.getTableNameBase();
    }

    @Override
    public boolean truncate() {
        return actions.truncate(getTableName());
    }

    @Override
    public DataEvent<T> createFor(String uniqueId, T data) {
        return actions.createBy(uniqueId, data);
    }

    @Override
    public List<T> getAllFor(String uniqueId) {
        return actions.findAllBy(uniqueId, getTableName());
    }

    @Override
    public T getLastFor(String uniqueId) {
        return actions.getLastBy(uniqueId);
    }

    @Override
    public long countFor(String uniqueId) {
        return actions.countBy(uniqueId, getTableName());
    }

    @Override
    public DataEvent<T> deleteFor(String uniqueId) {
        return actions.deleteBy(uniqueId);
    }

    @Override
    public boolean isDeleted(String uniqueId) {
        return actions.isDeletedBy(uniqueId);
    }
}