package com.holli.simple.datasourcing.contracts.actions;

import com.holli.simple.datasourcing.thread.*;

import java.util.function.*;

public interface DataActionsBase<T> extends DataActionsCommon<T> {

    boolean add(String uniqueId, T data);

    T getLast(String uniqueId);

    boolean delete(String uniqueId);

    ThreadMaster deleteInBackground(String uniqueId);

    ThreadMaster deleteInBackgroundCallback(String uniqueId, Consumer<Boolean> callback, Consumer<Exception> errorCallback);

    boolean isDeleted(String uniqueId);
}