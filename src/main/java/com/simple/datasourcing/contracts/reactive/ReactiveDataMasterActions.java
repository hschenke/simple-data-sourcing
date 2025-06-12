package com.simple.datasourcing.contracts.reactive;

import reactor.core.publisher.*;

public interface ReactiveDataMasterActions {

    <T> ReactiveDataActions<T> getDataActions(Class<T> clazz);

    Mono<Void> deleteAll(String uniqueId, Flux<ReactiveDataActions<?>> dataActions);
}