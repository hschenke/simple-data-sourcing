package com.simple.datasourcing.mongo;

import com.simple.datasourcing.interfaces.*;
import com.simple.datasourcing.model.*;
import lombok.*;
import org.springframework.data.mongodb.core.*;

@Getter
public class MongoDataActions<T> extends MongoDataActionsBase<T> implements DataActions<T> {

    public MongoDataActions(MongoTemplate mongo, Class<T> clazz) {
        super(mongo, clazz);
    }

    @Override
    public String getTableName() {
        return super.getTableNameBase();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataEvent<T> findLastBy(String uniqueId) {
        return super.getMongo().findOne(getQueryLastById(uniqueId), DataEvent.create().getClass(), getTableName());
    }

    @Override
    public DataEvent<T> insertBy(DataEvent<T> dataEvent) {
        return super.getMongo().insert(dataEvent, getTableName());
    }
}