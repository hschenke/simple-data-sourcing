package com.holli.simple.datasourcing.contracts.actions;

import com.holli.simple.datasourcing.model.*;

import java.util.*;

public interface DataActionsCommon<T> {

    String getTableName();

    boolean truncate();

    List<DataEvent<T>> getAll();

    List<T> getAll(String uniqueId);

    List<String> getAllIds();

    long count(String uniqueId);
}