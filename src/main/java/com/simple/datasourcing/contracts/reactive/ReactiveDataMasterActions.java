package com.simple.datasourcing.contracts.reactive;

public interface ReactiveDataMasterActions {

    <T> ReactiveDataActions<T> getDataActions(Class<T> clazz);
}