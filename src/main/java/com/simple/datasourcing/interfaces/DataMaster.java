package com.simple.datasourcing.interfaces;

public interface DataMaster {

    <T> DataActions<T> getDataActions(Class<T> clazz);

    <T> DataActionsHistory<T> getDataActionsHistory(Class<T> clazz);
}