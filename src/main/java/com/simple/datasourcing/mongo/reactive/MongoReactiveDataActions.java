package com.simple.datasourcing.mongo.reactive;

import com.simple.datasourcing.contracts.reactive.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class MongoReactiveDataActions<T> extends ReactiveDataActions<T> {

    @Getter
    private final MongoReactiveDataService<T> mongoService;

    public MongoReactiveDataActions(MongoReactiveDataService<T> service) {
        super(service);
        this.mongoService = service;
    }
}