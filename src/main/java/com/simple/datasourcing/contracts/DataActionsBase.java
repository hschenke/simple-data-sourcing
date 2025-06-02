package com.simple.datasourcing.contracts;

public interface DataActionsBase<T> extends DataActionsCommon<T> {

    boolean create(String uniqueId, T data);

    T getLast(String uniqueId);

    boolean delete(String uniqueId);

    boolean isDeleted(String uniqueId);
}