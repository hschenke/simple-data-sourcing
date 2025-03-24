package com.simple.datasourcing.interfaces;

public interface DataActionsHistory<T> extends DataActionsDefault<T> {

    default String getTableNameHistory(String tableNameDefault) {
        return tableNameDefault + "-history";
    }

    boolean doFullHistory(String uniqueId);

    boolean removeFor(String uniqueId);
}