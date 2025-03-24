package com.simple.datasourcing.interfaces;

import com.simple.datasourcing.model.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;

public interface DataActionsDefault<T> {

    default Query getQueryById(String uniqueId) {
        return new Query()
                .addCriteria(where("uniqueId").is(uniqueId));
    }

    List<DataEvent<T>> findAllBy(String uniqueId);

    long countBy(String uniqueId);

    default List<T> getAllFor(String uniqueId) {
        return findAllBy(uniqueId).stream().map(DataEvent::getData).toList();
    }

    default long countFor(String uniqueId) {
        return countBy(uniqueId);
    }

    default String getTableName(Class<T> clazz) {
        return clazz.getSimpleName();
    }
}