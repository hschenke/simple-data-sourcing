package com.simple.datasourcing.mongo.reactive;

import com.simple.datasourcing.contracts.reactive.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ReactiveMongoDataActions<T> extends ReactiveDataActions<T> {

    @Getter
    private final ReactiveMongoDataService<T> mongoService;

    public ReactiveMongoDataActions(ReactiveMongoDataService<T> service) {
        super(service);
        this.mongoService = service;
    }
}