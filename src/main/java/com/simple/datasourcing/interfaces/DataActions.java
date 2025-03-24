package com.simple.datasourcing.interfaces;

import com.simple.datasourcing.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.*;

import java.util.*;

public interface DataActions<T> extends DataActionsDefault<T> {

    default Query getQueryLastById(String uniqueId) {
        return getQueryById(uniqueId)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
    }

    DataEvent<T> findLastBy(String uniqueId);

    DataEvent<T> insertBy(DataEvent<T> dataEvent);

    default DataEvent<T> createFor(String uniqueId, T data) {
        return insertBy(DataEvent.<T>create().setData(uniqueId, false, data));
    }

    @Override
    default List<T> getAllFor(String uniqueId) {
        return isDeleted(uniqueId) ? List.of() : findAllBy(uniqueId).stream().map(DataEvent::getData).toList();
    }

    default T getLastFor(String uniqueId) {
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::getData)
                .orElse(null);
    }

    @Override
    default long countFor(String uniqueId) {
        return isDeleted(uniqueId) ? 0 : countBy(uniqueId);
    }

    default DataEvent<T> deleteFor(String uniqueId) {
        return insertBy(DataEvent.<T>create().setData(uniqueId, true, null));
    }

    default boolean isDeleted(String uniqueId) {
        return Optional.ofNullable(findLastBy(uniqueId))
                .map(DataEvent::isDeleted)
                .orElse(false);
    }
}