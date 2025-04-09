package com.simple.datasourcing.contracts;

import com.simple.datasourcing.model.*;

public interface DataActionsBase<T> extends DataActionsCommon<T> {

    DataEvent<T> createFor(String uniqueId, T data);

    T getLastFor(String uniqueId);

    DataEvent<T> deleteFor(String uniqueId);

    boolean isDeleted(String uniqueId);
}