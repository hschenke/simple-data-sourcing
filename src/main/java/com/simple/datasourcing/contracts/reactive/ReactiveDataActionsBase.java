package com.simple.datasourcing.contracts.reactive;

import reactor.core.publisher.*;

public interface ReactiveDataActionsBase<T> extends ReactiveDataActionsCommon<T> {

    Mono<Boolean> createFor(String uniqueId, T data);

    Mono<T> getLastFor(String uniqueId);

    Mono<Boolean> deleteFor(String uniqueId);

    Mono<Boolean> isDeleted(String uniqueId);
}