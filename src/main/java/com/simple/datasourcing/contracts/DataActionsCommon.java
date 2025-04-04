package com.simple.datasourcing.contracts;

import java.util.*;

public interface DataActionsCommon<T> {

    String getTableName();

    boolean truncate();

    List<T> getAllFor(String uniqueId);

    long countFor(String uniqueId);
}