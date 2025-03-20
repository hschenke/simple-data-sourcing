package com.simple.datasourcing.service;

import lombok.*;

@Getter
public class DataMaster {

    private final DataCon dataCon;

    public DataMaster(String mongoUri) {
        dataCon = new DataCon(mongoUri);
    }

    public <T> DataActions<T> getDataActions(Class<T> clazz) {
        return new DataActions<>(dataCon, clazz);
    }

    public <T> DataActionsHistory<T> getDataActionsHistory(Class<T> clazz) {
        return new DataActionsHistory<>(dataCon, clazz);
    }
}