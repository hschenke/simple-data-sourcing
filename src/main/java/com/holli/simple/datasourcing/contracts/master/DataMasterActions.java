package com.holli.simple.datasourcing.contracts.master;

import com.holli.simple.datasourcing.contracts.actions.*;
import com.holli.simple.datasourcing.thread.*;

public interface DataMasterActions {

    <T> DataActions<T> getDataActions(Class<T> clazz);

    void deleteAll(String uniqueId, DataActions<?>... dataActions);

    ThreadMaster deleteAllInBackground(String uniqueId, DataActions<?>... dataActions);
}