package com.simple.datasourcing.contracts;

public interface DataActionsHistory<T> extends DataActionsCommon<T> {

    boolean doFullHistory(String uniqueId);

    boolean removeFor(String uniqueId);
}