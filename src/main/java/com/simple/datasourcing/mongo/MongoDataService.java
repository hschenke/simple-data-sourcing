package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.service.*;
import com.simple.datasourcing.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;

public class MongoDataService<T> extends DataService<T, MongoTemplate, Query> {

    public MongoDataService(MongoDataConnection mongoDataConnection, Class<T> clazz) {
        super(mongoDataConnection, clazz);
    }

    @Override
    public void createBaseTable() {
        dataTemplate().createCollection(getTableNameBase());
    }

    @Override
    public void createHistoryTable() {
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
        return dataTemplate().remove(new Query(), tableName).wasAcknowledged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DataEvent<T>> findAll(String tableName) {
        return (List<DataEvent<T>>) dataTemplate().findAll(DataEvent.create().getClass(), tableName);
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
    public Long countBy(String uniqueId, String tableName) {
        return dataTemplate().count(getQueryById(uniqueId), tableName);
    }

    @Override
    public boolean removeBy(String uniqueId, String tableName) {
        return dataTemplate().remove(getQueryById(uniqueId), tableName).wasAcknowledged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataEvent<T> findLastBy(String uniqueId) {
        return dataTemplate().findOne(getQueryLastById(uniqueId), DataEvent.create().getClass(), getTableNameBase());
    }

    @Override
    public boolean insertBy(DataEvent<T> dataEvent) {
        return dataTemplate().insert(dataEvent, getTableNameBase()).getUniqueId() != null;
    }

    @Override
    public boolean moveToHistory(String uniqueId) {
        var bulkOpsHistory = dataTemplate().bulkOps(BulkOperations.BulkMode.ORDERED, getTableNameHistory());
        bulkOpsHistory.insert(findAllEventsBy(uniqueId, getTableNameBase()));
        return bulkOpsHistory.execute().wasAcknowledged();
    }

    @Override
    public boolean removeFromBase(String uniqueId) {
        var bulkOps = dataTemplate().bulkOps(BulkOperations.BulkMode.UNORDERED, getTableNameBase());
        bulkOps.remove(getQueryById(uniqueId));
        return bulkOps.execute().wasAcknowledged();
    }
}