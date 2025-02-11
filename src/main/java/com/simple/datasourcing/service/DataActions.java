package com.simple.datasourcing.service;

import com.simple.datasourcing.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static com.simple.datasourcing.config.ConfigReader.*;

public class DataActions<T> {

    private final Class<T> clazz;
    private final DataEvent<T> event;
    SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(getProperty("mongodb.url"));
    MongoTemplate template = new MongoTemplate(factory);

    public DataActions(Class<T> clazz) {
        this.clazz = clazz;
        template.createCollection(getTableName());
        event = new DataEvent<>();
    }

    public List<DataEvent> findByField(String field, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where(field).is(value));
        return template.find(query, DataEvent.class, getTableName());
    }

    public List<T> getAllFor(String uniqueId) {
        return (List<T>) findByField("uniqueId", uniqueId).stream().map(DataEvent::getData).toList();
    }


    public T getLastFor(String uniqueId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uniqueId").is(uniqueId));
        query.with(Sort.by(Sort.Direction.DESC, "timestamp")); // Sortiert nach Timestamp absteigend
        query.limit(1);
        return (T) Optional.of(template.findOne(query, DataEvent.class, getTableName()))
                .map(DataEvent::getData)
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