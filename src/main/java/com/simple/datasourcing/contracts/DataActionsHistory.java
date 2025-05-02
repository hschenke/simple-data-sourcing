package com.simple.datasourcing.contracts;

public interface DataActionsHistory<T> extends DataActionsCommon<T> {

    boolean dataHistorization(String uniqueId, boolean includeDelete);

    boolean removeFor(String uniqueId);
}