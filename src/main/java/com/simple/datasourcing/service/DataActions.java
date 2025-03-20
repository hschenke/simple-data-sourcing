package com.simple.datasourcing.service;

import com.simple.datasourcing.model.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;

public class DataActions<T> {

    private final Class<T> clazz;
    final DataEvent<T> event;
    DataCon dataCon;
    @Getter
    private final String tableName;

    public DataActions(DataCon dataCon, Class<T> clazz) {
        this.dataCon = dataCon;
        this.clazz = clazz;
        this.tableName = clazz.getSimpleName();
        dataCon.getTemplate().createCollection(tableName);
        event = DataEvent.create();
    }

    public DataActions(String mongoUri, Class<T> clazz) {
        this(new DataCon(mongoUri), clazz);
    }

    Query getQueryById(Object value) {
        return new Query()
                .addCriteria(where("uniqueId").is(value));
    }

    Query getQueryLastById(String uniqueId) {
        return getQueryById(uniqueId)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
    }

    List<DataEvent<T>> findById(Object uniqueId) {
        return findById(uniqueId, tableName);
    }

    @SuppressWarnings("unchecked")
    List<DataEvent<T>> findById(Object uniqueId, String tableName) {
        return (List<DataEvent<T>>) dataCon.getTemplate().find(getQueryById(uniqueId), event.getClass(), tableName);
    }

    public DataEvent<T> createFor(String uniqueId, T data) {
        return dataCon.getTemplate().insert(event.setData(uniqueId, false, data), tableName);
    }

    public List<T> getAllFor(String uniqueId) {
        return isDeleted(uniqueId) ? List.of() : findById(uniqueId).stream().map(DataEvent::getData).toList();
    }

    public T getLastFor(String uniqueId) {
        return Optional.ofNullable(
                        dataCon.getTemplate().findOne(getQueryLastById(uniqueId), DataEvent.class, tableName)
                )
                .map(de -> clazz.cast(de.getData()))
                .orElse(null);
    }

    public long countFor(String uniqueId) {
        return isDeleted(uniqueId) ? 0 : dataCon.getTemplate().count(getQueryById(uniqueId), tableName);
    }

    public DataEvent<T> deleteFor(String uniqueId) {
        return dataCon.getTemplate().insert(event.setData(uniqueId, true, null), tableName);
    }

    public boolean isDeleted(String uniqueId) {
        return Optional.ofNullable(
                        dataCon.getTemplate().findOne(getQueryLastById(uniqueId), DataEvent.class, tableName)
                )
                .filter(DataEvent::isDeleted)
                .isPresent();
    }
}