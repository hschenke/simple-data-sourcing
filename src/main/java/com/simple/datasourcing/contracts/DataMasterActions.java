package com.simple.datasourcing.contracts;

public interface DataMasterActions {

    <T> DataActions<T> getDataActions(Class<T> clazz);
}