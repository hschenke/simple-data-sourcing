package com.simple.datasourcing.contracts.actions;

import java.util.*;

public interface DataActionsCommon<T> {

    String getTableName();

    boolean truncate();

    List<T> getAll(String uniqueId);

    long count(String uniqueId);
}