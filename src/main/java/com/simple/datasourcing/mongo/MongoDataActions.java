package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import com.simple.datasourcing.model.*;
import lombok.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Getter
public class MongoDataActions<T> extends MongoDataActionsBase<T> implements DataActions<T> {

    public MongoDataActions(MongoTemplate mongo, Class<T> clazz) {
        super(mongo, clazz);
    }

    @Override
    public String getTableName() {
        return getTableNameBase();
    }

    @Override
    public boolean truncate() {
        return truncate(getTableName());
    }

    @Override
    public DataEvent<T> createFor(String uniqueId, T data) {
        return insertBy(DataEvent.<T>create().setData(uniqueId, false, data));
    }

    @Override
    public List<T> getAllFor(String uniqueId) {
        return isDeleted(uniqueId) ? List.of() : findAllBy(uniqueId, getTableName());
    }

    @Override
    public T getLastFor(String uniqueId) {
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::getData)
                .orElse(null);
    }

    @Override
    public long countFor(String uniqueId) {
        return isDeleted(uniqueId) ? 0 : countBy(uniqueId, getTableName());
    }

    @Override
    public DataEvent<T> deleteFor(String uniqueId) {
        return insertBy(DataEvent.<T>create().setData(uniqueId, true, null));
    }

    @Override
    public boolean isDeleted(String uniqueId) {
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::isDeleted)
                .orElse(false);
    }
}