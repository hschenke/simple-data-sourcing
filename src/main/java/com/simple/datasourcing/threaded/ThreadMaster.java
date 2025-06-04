package com.simple.datasourcing.threaded;

import java.util.function.*;

public class ThreadMaster {

    private ThreadMaster() {
    }

    public static ThreadMaster get() {
        return new ThreadMaster();
    }

    public <T> void virtualWithCallback(Supplier<T> action,
                                        Consumer<T> callback,
                                        Consumer<Exception> error) {
        Thread.ofVirtual().start(() -> {
            try {
                callback.accept(action.get());
            } catch (Exception e) {
                error.accept(e);
            }
        });
    }

    public static <T> CallbackOrErrorOrExecuteSetter<T> action(Supplier<T> action) {
        return new ActionBuilder<>(action);
    }

    public interface CallbackOrErrorOrExecuteSetter<T> {
        CallbackOrErrorOrExecuteSetter<T> callback(Consumer<T> callback);

        CallbackOrErrorOrExecuteSetter<T> onError(Consumer<Exception> error);

        void execute();
    }

    private static class ActionBuilder<T> implements CallbackOrErrorOrExecuteSetter<T> {

        private final Supplier<T> action;
        private Consumer<T> callback = _ -> {
        };
        private Consumer<Exception> error = _ -> {
        };

        public ActionBuilder(Supplier<T> action) {
            this.action = action;
        }

        @Override
        public void execute() {
            ThreadMaster.get().virtualWithCallback(action, callback, error);
        }

        @Override
        public ActionBuilder<T> callback(Consumer<T> callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public ActionBuilder<T> onError(Consumer<Exception> error) {
            this.error = error;
            return this;
        }
    }
}