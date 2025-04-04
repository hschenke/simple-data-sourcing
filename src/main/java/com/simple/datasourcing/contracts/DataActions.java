package com.simple.datasourcing.contracts;

import com.simple.datasourcing.model.*;

public interface DataActions<T> extends DataActionsCommon<T> {

    DataEvent<T> createFor(String uniqueId, T data);

    T getLastFor(String uniqueId);

    DataEvent<T> deleteFor(String uniqueId);

    boolean isDeleted(String uniqueId);

//    default DataEvent<T> createFor(String uniqueId, T data) {
//        return insertBy(DataEvent.<T>create().setData(uniqueId, false, data));
//    }
//
//    default List<T> getAllFor(String uniqueId) {
//        return isDeleted(uniqueId) ? List.of() : findAllBy(uniqueId);
//    }
//
//    default T getLastFor(String uniqueId) {
//        return Optional.ofNullable(findLastBy(uniqueId))
//                .map(DataEvent::getData)
//                .orElse(null);
//    }
//
//    default long countFor(String uniqueId) {
//        return isDeleted(uniqueId) ? 0 : countBy(uniqueId, getTableName());
//    }
//
//    default DataEvent<T> deleteFor(String uniqueId) {
//        return insertBy(DataEvent.<T>create().setData(uniqueId, true, null));
//    }
//
//    default boolean isDeleted(String uniqueId) {
//        return Optional.ofNullable(findLastBy(uniqueId))
//                .map(DataEvent::isDeleted)
//                .orElse(false);
//    }
}