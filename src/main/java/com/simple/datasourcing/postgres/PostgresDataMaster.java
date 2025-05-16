package com.simple.datasourcing.postgres;

import com.simple.datasourcing.contracts.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public class PostgresDataMaster extends DataMaster {

    public PostgresDataMaster(String dbUri) {
        super(dbUri);
    }

    @Override
    protected PostgresDataConnection generateDataConnection() {
        return new PostgresDataConnection(getDbUri());
    }

    @Override
    protected <T> PostgresDataService<T> getDataService(Class<T> clazz) {
        return new PostgresDataService<>(generateDataConnection(), clazz);
    }

    @Override
    public <T> PostgresDataActions<T> getDataActions(Class<T> clazz) {
        return new PostgresDataActions<>(getDataService(clazz));
    }
}