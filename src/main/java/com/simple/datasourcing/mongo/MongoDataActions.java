package com.simple.datasourcing.mongo;

import com.simple.datasourcing.model.*;
import com.simple.datasourcing.interfaces.*;
import lombok.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Getter
public class MongoDataActions<T> implements DataActions<T> {

    private final MongoTemplate mongo;
    private final String tableName;

    public MongoDataActions(MongoTemplate mongo, Class<T> clazz) {
        this.mongo = mongo;
        this.tableName = getTableName(clazz);
        mongo.createCollection(tableName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DataEvent<T>> findAllBy(String uniqueId) {
        return (List<DataEvent<T>>) mongo.find(getQueryById(uniqueId), DataEvent.create().getClass(), tableName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataEvent<T> findLastBy(String uniqueId) {
        return mongo.findOne(getQueryLastById(uniqueId), DataEvent.create().getClass(), tableName);
    }

    @Override
    public DataEvent<T> insertBy(DataEvent<T> dataEvent) {
        return mongo.insert(dataEvent, tableName);
    }

    @Override
    public long countBy(String uniqueId) {
        return mongo.count(getQueryById(uniqueId), tableName);
    }
}