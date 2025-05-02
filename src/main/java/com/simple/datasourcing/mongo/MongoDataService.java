package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import com.simple.datasourcing.model.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;

@Slf4j
public class MongoDataService<T> extends DataService<T, MongoTemplate, Query> {

    public MongoDataService(Class<T> clazz, MongoTemplate dataTemplate) {
        super(clazz, dataTemplate);
    }

    @Override
    protected void createTables() {
        getDataTemplate().createCollection(getTableNameBase());
        getDataTemplate().createCollection(getTableNameHistory());
    }

    @Override
    protected Query getQueryById(String uniqueId) {
        return new Query()
                .addCriteria(where("uniqueId").is(uniqueId));
    }

    @Override
    protected Query getQueryLastById(String uniqueId) {
        return getQueryById(uniqueId)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
    }

    @Override
    protected boolean truncate(String tableName) {
        log.info("Truncating table {}", tableName);
        return getDataTemplate().remove(new Query(), tableName).wasAcknowledged();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName) {
        return (List<DataEvent<T>>) getDataTemplate().find(getQueryById(uniqueId), DataEvent.create().getClass(), tableName);
    }

    @Override
    protected boolean tableExists(String tableName) {
        return getDataTemplate().collectionExists(tableName);
    }

    @Override
    protected long countBy(String uniqueId, String tableName) {
        return getDataTemplate().count(getQueryById(uniqueId), tableName);
    }

    @Override
    protected boolean removeBy(String uniqueId, String tableName) {
        return getDataTemplate().remove(getQueryById(uniqueId), getTableNameHistory()).wasAcknowledged();
    }

    @Override
    protected boolean dataHistorization(String uniqueId, boolean includeDelete) {
        try {
            var bulkOpsHistory = getDataTemplate().bulkOps(BulkOperations.BulkMode.ORDERED, getTableNameHistory());
            bulkOpsHistory.insert(
                    findAllEventsBy(uniqueId, getTableNameBase()).stream()
                            .filter(event -> includeDelete || !event.isDeleted())
                            .toList()
            );
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

    @SuppressWarnings("unchecked")
    @Override
    protected DataEvent<T> findLastBy(String uniqueId) {
        return getDataTemplate().findOne(getQueryLastById(uniqueId), DataEvent.create().getClass(), getTableNameBase());
    }

    @Override
    protected DataEvent<T> insertBy(DataEvent<T> dataEvent) {
        return getDataTemplate().insert(dataEvent, getTableNameBase());
    }
}