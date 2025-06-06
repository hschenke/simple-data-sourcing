package com.simple.datasourcing.contracts.actions;

import com.simple.datasourcing.thread.*;

public interface DataActionsBase<T> extends DataActionsCommon<T> {

    boolean create(String uniqueId, T data);

    T getLast(String uniqueId);

    boolean delete(String uniqueId);

    ThreadMaster deleteInBackground(String uniqueId);

    ThreadDataAction<Boolean> deleteInBackgroundCallback(String uniqueId);

    boolean isDeleted(String uniqueId);
}