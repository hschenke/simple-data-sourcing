package com.simple.datasourcing.contracts.reactive;

import com.simple.datasourcing.model.*;
import reactor.core.publisher.*;

public interface ReactiveDataActionsCommon<T> {

    String getTableName();

    Mono<Boolean> truncate();

    Flux<DataEvent<T>> getAll();

    Flux<T> getAll(String uniqueId);

    Flux<String> getAllIds(String uniqueId);

    Mono<Long> count(String uniqueId);
}