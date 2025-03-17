package com.simple.datasourcing.service;

import com.simple.datasourcing.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

public class DataActions<T> {

    private final Class<T> clazz;
    private final DataEvent<T> event;
    private final MongoTemplate template;

    public DataActions(String mongoUri, Class<T> clazz) {
        this.clazz = clazz;
        var factory = new SimpleMongoClientDatabaseFactory(mongoUri);
        template = new MongoTemplate(factory);
        template.createCollection(getTableName());
        template.createCollection(getTableName() + "-history");
        event = DataEvent.create();
    }

    private Query getQueryById(Object value) {
        var query = new Query();
        query.addCriteria(Criteria.where("uniqueId").is(value));
        return query;
    }

    @SuppressWarnings("unchecked")
    public List<DataEvent<T>> findById(Object value) {
        return (List<DataEvent<T>>) template.find(getQueryById(value), event.getClass(), getTableName());
    }

    public List<T> getAllFor(String uniqueId) {
        return findById(uniqueId).stream().map(DataEvent::getData).toList();
    }

    public T getLastFor(String uniqueId) {
        var query = getQueryById(uniqueId);
        query.with(Sort.by(Sort.Direction.DESC, "timestamp")); // Sortiert nach Timestamp absteigend
        query.limit(1);
        return Optional.ofNullable(template.findOne(query, DataEvent.class, getTableName()))
                .map(de -> clazz.cast(de.getData()))
                .orElse(null);
    }

    public DataEvent<T> createFor(String uniqueId, T data) {
        return template.insert(event.setData(uniqueId, false, data), getTableName());
    }

    public DataEvent<T> deleteFor(String uniqueId) {
        return template.insert(event.setData(uniqueId, true, null), getTableName());
    }

    public String getTableName() {
        return clazz.getSimpleName();
    }
}