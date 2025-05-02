package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Getter
@Slf4j
public class MongoDataActions<T> extends DataActions<T> {

    public MongoDataActions(MongoDataService<T> service) {
        super(service);
    }
}