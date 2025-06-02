package com.simple.datasourcing.contracts;

import com.simple.datasourcing.model.*;

import java.util.*;

public interface DataServiceActions<T, Q> {

    void createBaseTable();

    void createHistoryTable();

    Q getQueryById(String uniqueId);

    Q getQueryLastById(String uniqueId);

    boolean truncate(String tableName);

    List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName);

    boolean tableExists(String tableName);

    Long countBy(String uniqueId, String tableName);

    boolean removeBy(String uniqueId, String tableName);

    DataEvent<T> findLastBy(String uniqueId);

    boolean insertBy(DataEvent<T> dataEvent);

    boolean moveToHistory(String uniqueId);

    boolean removeFromBase(String uniqueId);
}