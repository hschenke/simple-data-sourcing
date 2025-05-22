package com.simple.datasourcing.contracts.reactive;

import reactor.core.publisher.*;

public interface ReactiveDataActionsCommon<T> {

    String getTableName();

    Mono<Boolean> truncate();

    Flux<T> getAllFor(String uniqueId);

    Mono<Long> countFor(String uniqueId);
}