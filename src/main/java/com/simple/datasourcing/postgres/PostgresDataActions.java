package com.simple.datasourcing.postgres;

import com.simple.datasourcing.contracts.actions.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public class PostgresDataActions<T> extends DataActions<T> {

    private final PostgresDataService<T> service;

    public PostgresDataActions(PostgresDataService<T> service) {
        super(service);
        this.service = service;
    }
}