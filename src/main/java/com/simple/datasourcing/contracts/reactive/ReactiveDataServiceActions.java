package com.simple.datasourcing.contracts.reactive;

import com.mongodb.client.result.*;
import com.simple.datasourcing.model.*;
import reactor.core.publisher.*;

public interface ReactiveDataServiceActions<T, Q> {

    Mono<String> createBaseTable();

    Mono<String> createHistoryTable();

    Q getQueryById(String uniqueId);

    Q getQueryLastById(String uniqueId);

    Mono<Boolean> truncate(String tableName);

    Flux<DataEvent<T>> findAll(String tableName);

    Flux<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName);

    Mono<Boolean> tableExists(String tableName);

    Mono<Long> countBy(String uniqueId, String tableName);

    Mono<DeleteResult> removeBy(String uniqueId, String tableName);

    Mono<DataEvent<T>> findLastBy(String uniqueId);

    Mono<DataEvent<T>> insertBy(DataEvent<T> dataEvent);

    Mono<Boolean> moveToHistory(String uniqueId);

    Mono<Boolean> removeFromBase(String uniqueId);
}