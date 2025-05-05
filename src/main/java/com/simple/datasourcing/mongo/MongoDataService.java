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

    public MongoDataService(MongoDataConnection mongoDataConnection, Class<T> clazz) {
        super(mongoDataConnection, clazz);
    }

    @Override
    public void createTables() {
        dataTemplate().createCollection(getTableNameBase());
        dataTemplate().createCollection(getTableNameHistory());
    }

    @Override
    public Query getQueryById(String uniqueId) {
        return new Query()
                .addCriteria(where("uniqueId").is(uniqueId));
    }

    @Override
    public Query getQueryLastById(String uniqueId) {
        return getQueryById(uniqueId)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
    }

    @Override
    public boolean truncate(String tableName) {
        log.info("Truncating table {}", tableName);
        return dataTemplate().remove(new Query(), tableName).wasAcknowledged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName) {
        return (List<DataEvent<T>>) dataTemplate().find(getQueryById(uniqueId), DataEvent.create().getClass(), tableName);
    }

    @Override
    public boolean tableExists(String tableName) {
        return dataTemplate().collectionExists(tableName);
    }

    @Override
    public long countBy(String uniqueId, String tableName) {
        return dataTemplate().count(getQueryById(uniqueId), tableName);
    }

    @Override
    public boolean removeBy(String uniqueId, String tableName) {
        return dataTemplate().remove(getQueryById(uniqueId), getTableNameHistory()).wasAcknowledged();
    }

    @Override
    public boolean dataHistorization(String uniqueId, boolean includeDelete) {
        try {
            var bulkOpsHistory = dataTemplate().bulkOps(BulkOperations.BulkMode.ORDERED, getTableNameHistory());
            bulkOpsHistory.insert(
                    findAllEventsBy(uniqueId, getTableNameBase()).stream()
                            .filter(event -> includeDelete || !event.isDeleted())
                            .toList()
            );
            bulkOpsHistory.execute();

            var bulkOps = dataTemplate().bulkOps(BulkOperations.BulkMode.UNORDERED, getTableNameBase());
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
    public DataEvent<T> findLastBy(String uniqueId) {
        return dataTemplate().findOne(getQueryLastById(uniqueId), DataEvent.create().getClass(), getTableNameBase());
    }

    @Override
    public DataEvent<T> insertBy(DataEvent<T> dataEvent) {
        return dataTemplate().insert(dataEvent, getTableNameBase());
    }
}