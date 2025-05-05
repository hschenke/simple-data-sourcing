package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
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