package com.simple.datasourcing.contracts.reactive;

import reactor.core.publisher.*;

public interface ReactiveDataActionsHistory<T> extends ReactiveDataActionsCommon<T> {

    Mono<Boolean> dataHistorization(String uniqueId);

    Mono<Boolean> removeFor(String uniqueId);
}