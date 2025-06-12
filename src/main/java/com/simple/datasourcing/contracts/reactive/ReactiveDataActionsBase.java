package com.simple.datasourcing.contracts.reactive;

import reactor.core.publisher.*;

public interface ReactiveDataActionsBase<T> extends ReactiveDataActionsCommon<T> {

    Mono<Boolean> add(String uniqueId, T data);

    Mono<T> getLast(String uniqueId);

    Mono<Boolean> delete(String uniqueId);

    Mono<Boolean> isDeleted(String uniqueId);
}