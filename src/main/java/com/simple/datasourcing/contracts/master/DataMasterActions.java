package com.simple.datasourcing.contracts.master;

import com.simple.datasourcing.contracts.actions.*;

public interface DataMasterActions {

    <T> DataActions<T> getDataActions(Class<T> clazz);
}