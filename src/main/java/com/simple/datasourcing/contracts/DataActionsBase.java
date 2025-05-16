package com.simple.datasourcing.contracts;

public interface DataActionsBase<T> extends DataActionsCommon<T> {

    boolean createFor(String uniqueId, T data);

    T getLastFor(String uniqueId);

    boolean deleteFor(String uniqueId);

    boolean isDeleted(String uniqueId);
}