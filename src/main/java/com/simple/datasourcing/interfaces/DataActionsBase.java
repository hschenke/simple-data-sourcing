package com.simple.datasourcing.interfaces;

import com.simple.datasourcing.model.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;

public interface DataActionsBase<T> {

    String getTableName();

    default Query getQueryById(String uniqueId) {
        return new Query()
                .addCriteria(where("uniqueId").is(uniqueId));
    }

    default boolean truncate() {
        return truncate(getTableName());
    }

    boolean truncate(String tableName);

    List<DataEvent<T>> findAllBy(String uniqueId, String tableName);

    default List<T> findAllBy(String uniqueId) {
        return findAllBy(uniqueId, getTableName()).stream().map(DataEvent::getData).toList();
    }

    long countBy(String uniqueId, String tableName);

    default String getTableNameBase(Class<T> clazz) {
        return clazz.getSimpleName();
    }

    default String getTableNameHistory(Class<T> clazz) {
        return getTableNameBase(clazz) + "-history";
    }
}