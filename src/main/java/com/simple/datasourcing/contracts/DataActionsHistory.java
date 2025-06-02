package com.simple.datasourcing.contracts;

public interface DataActionsHistory<T> extends DataActionsCommon<T> {

    boolean historization(String uniqueId);

    boolean remove(String uniqueId);
}