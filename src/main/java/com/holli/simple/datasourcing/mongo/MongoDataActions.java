package com.holli.simple.datasourcing.mongo;

import com.holli.simple.datasourcing.contracts.actions.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public class MongoDataActions<T> extends DataActions<T> {

    private final MongoDataService<T> service;

    public MongoDataActions(MongoDataService<T> service) {
        super(service);
        this.service = service;
    }
}