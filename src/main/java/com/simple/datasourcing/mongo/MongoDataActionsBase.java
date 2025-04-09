package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import com.simple.datasourcing.model.*;
import lombok.*;

import java.util.*;

@Getter
public class MongoDataActionsBase<T> extends MongoDataActions<T> implements DataActionsBase<T> {

    public MongoDataActionsBase(MongoDataActions<T> mongoDataActions) {
        super(mongoDataActions.getDataTemplate(), mongoDataActions.getClazz());
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
        return createBy(uniqueId, data);
    }

    @Override
    public List<T> getAllFor(String uniqueId) {
        return getAllBaseBy(uniqueId);
    }

    @Override
    public T getLastFor(String uniqueId) {
        return getLastBy(uniqueId);
    }

    @Override
    public long countFor(String uniqueId) {
        return countBaseBy(uniqueId);
    }

    @Override
    public DataEvent<T> deleteFor(String uniqueId) {
        return deleteBy(uniqueId);
    }

    @Override
    public boolean isDeleted(String uniqueId) {
        return isDeletedBy(uniqueId);
    }
}