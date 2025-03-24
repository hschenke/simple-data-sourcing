package com.simple.datasourcing.interfaces;

import java.util.*;

public interface DataActionsHistory<T> extends DataActionsBase<T> {

    boolean doFullHistory(String uniqueId);

    boolean removeFor(String uniqueId);

    default List<T> getAllFor(String uniqueId) {
        return findAllBy(uniqueId);
    }

    default long countFor(String uniqueId) {
        return countBy(uniqueId, getTableName());
    }
}