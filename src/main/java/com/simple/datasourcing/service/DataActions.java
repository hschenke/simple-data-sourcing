package com.simple.datasourcing.service;

import com.simple.datasourcing.model.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;

public class DataActions<T> {

    private final Class<T> clazz;
    final DataEvent<T> event;
    final MongoTemplate template;
    @Getter
    private final String tableName;

    public DataActions(String mongoUri, Class<T> clazz) {
        this.clazz = clazz;
        this.tableName = clazz.getSimpleName();
        var factory = new SimpleMongoClientDatabaseFactory(mongoUri);
        template = new MongoTemplate(factory);
        template.createCollection(tableName);
        event = DataEvent.create();
    }

    Query getQueryById(Object value) {
        return new Query()
                .addCriteria(where("uniqueId").is(value));
    }

    private Query getQueryLastById(String uniqueId) {
        return getQueryById(uniqueId)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
    }

    @SuppressWarnings("unchecked")
    List<DataEvent<T>> findById(Object value) {
        return (List<DataEvent<T>>) template.find(getQueryById(value), event.getClass(), tableName);
    }

    public List<T> getAllFor(String uniqueId) {
        return isDeleted(uniqueId) ? List.of() : findById(uniqueId).stream().map(DataEvent::getData).toList();
    }

    public T getLastFor(String uniqueId) {
        return Optional.ofNullable(
                        template.findOne(getQueryLastById(uniqueId), DataEvent.class, tableName)
                )
                .map(de -> clazz.cast(de.getData()))
                .orElse(null);
    }

    public DataEvent<T> createFor(String uniqueId, T data) {
        return template.insert(event.setData(uniqueId, false, data), tableName);
    }

    public DataEvent<T> deleteFor(String uniqueId) {
        return template.insert(event.setData(uniqueId, true, null), tableName);
    }

    public long countFor(String uniqueId) {
        return isDeleted(uniqueId) ? 0 : template.count(getQueryById(uniqueId), tableName);
    }

    public boolean isDeleted(String uniqueId) {
        return Optional.ofNullable(
                        template.findOne(getQueryLastById(uniqueId), DataEvent.class, tableName)
                )
                .filter(DataEvent::isDeleted)
                .isPresent();
    }
}