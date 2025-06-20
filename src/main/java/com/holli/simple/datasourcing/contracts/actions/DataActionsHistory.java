package com.holli.simple.datasourcing.contracts.actions;

import com.holli.simple.datasourcing.thread.*;

import java.util.function.*;

public interface DataActionsHistory<T> extends DataActionsCommon<T> {

    boolean historization(String uniqueId);

    ThreadMaster historizationInBackground(String uniqueId);

    ThreadMaster historizationInBackgroundCallback(String uniqueId, Consumer<Boolean> callback, Consumer<Exception> errorCallback);

    boolean remove(String uniqueId);

    ThreadMaster removeInBackground(String uniqueId);

    ThreadMaster removeInBackgroundCallback(String uniqueId, Consumer<Boolean> callback, Consumer<Exception> errorCallback);
}