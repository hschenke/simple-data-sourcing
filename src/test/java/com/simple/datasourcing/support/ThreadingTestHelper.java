package com.simple.datasourcing.support;

import lombok.extern.slf4j.*;

import java.time.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.awaitility.Awaitility.*;

@Slf4j
public class ThreadingTestHelper<T> {

    private final AtomicReference<T> successResult = new AtomicReference<>();
    private final AtomicReference<Exception> errorResult = new AtomicReference<>();
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public Consumer<T> getSuccessCallback() {
        return result -> {
            log.info("Success :: {} :: result :: {}", result.getClass().getSimpleName(), result);
            successResult.set(result);
            completed.set(true);
        };
    }

    public Consumer<Exception> getErrorCallback() {
        return error -> {
            log.info("Error :: {}", String.valueOf(error));
            errorResult.set(error);
            completed.set(true);
        };
    }

    public void awaitCompletion(Duration timeout) {
        await().atMost(timeout).until(completed::get);
    }

    public T getSuccessResult() {
        return successResult.get();
    }

    public Exception getErrorResult() {
        return errorResult.get();
    }

    public boolean wasSuccessful() {
        return successResult.get() != null && errorResult.get() == null;
    }

    public boolean hadError() {
        return errorResult.get() != null;
    }

    public static <T> CompletableFuture<T> captureCallbackResult(Consumer<Consumer<T>> callbackInvoker) {
        CompletableFuture<T> future = new CompletableFuture<>();
        callbackInvoker.accept(future::complete);
        return future;
    }

    public static <T> T awaitCallbackResult(Consumer<Consumer<T>> callbackInvoker, Duration timeout) {
        return captureCallbackResult(callbackInvoker)
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .join();
    }
}