package com.simple.datasourcing.contracts.actions;

import com.simple.datasourcing.thread.*;

public interface DataActionsHistory<T> extends DataActionsCommon<T> {

    boolean historization(String uniqueId);

    ThreadMaster historizationInBackground(String uniqueId);

    ThreadDataAction<Boolean> historizationInBackgroundCallback(String uniqueId);

    boolean remove(String uniqueId);

    ThreadMaster removeInBackground(String uniqueId);

    ThreadDataAction<Boolean> removeInBackgroundCallback(String uniqueId);
}