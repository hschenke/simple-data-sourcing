package com.simple.datasourcing.contracts.reactive;

import reactor.core.publisher.*;

public interface ReactiveDataActionsHistory<T> extends ReactiveDataActionsCommon<T> {

    Mono<Boolean> historization(String uniqueId);

    Mono<Boolean> remove(String uniqueId);
}