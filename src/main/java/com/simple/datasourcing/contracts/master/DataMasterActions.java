package com.simple.datasourcing.contracts.master;

import com.simple.datasourcing.contracts.actions.*;
import com.simple.datasourcing.thread.*;

public interface DataMasterActions {

    <T> DataActions<T> getDataActions(Class<T> clazz);

    void deleteAll(String uniqueId, DataActions<?>... dataActions);

    ThreadMaster deleteAllInBackground(String uniqueId, DataActions<?>... dataActions);
}