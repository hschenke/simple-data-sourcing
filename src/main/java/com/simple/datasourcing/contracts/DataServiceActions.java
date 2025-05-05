package com.simple.datasourcing.contracts;

import com.simple.datasourcing.model.*;

import java.util.*;

public interface DataServiceActions<T, Q> {

    void createTables();

    Q getQueryById(String uniqueId);

    Q getQueryLastById(String uniqueId);

    boolean truncate(String tableName);

    List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName);

    boolean tableExists(String tableName);

    long countBy(String uniqueId, String tableName);

    boolean removeBy(String uniqueId, String tableName);

    boolean dataHistorization(String uniqueId, boolean includeDelete);

    DataEvent<T> findLastBy(String uniqueId);

    DataEvent<T> insertBy(DataEvent<T> dataEvent);
}