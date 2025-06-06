package com.simple.datasourcing;

import com.simple.datasourcing.model.*;
import com.simple.datasourcing.support.*;
import com.simple.datasourcing.thread.*;
import org.jetbrains.annotations.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

class ThreadingTests extends TestDataAndSetup {

    static Stream<TestCase<?>> testData() {
        var testData1DataEvent = DataEvent.<TestData1>create().setDataset("holli", false, new TestData1("1", "name", Boolean.TRUE));
        return Stream.of(
                new TestCase<>(() -> "String Result", "String Result"),
                new TestCase<>(() -> 42, 42),
                new TestCase<>(() -> Boolean.TRUE, Boolean.TRUE),
                new TestCase<>(() -> testData1DataEvent, testData1DataEvent),
                new TestCase<>(() -> List.of(1, 2, 3), List.of(1, 2, 3)),
                new TestCase<>(() -> Map.of("key", "value"), Map.of("key", "value"))
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    <T> void testVariousTypes(TestCase<T> testCase) {
        var threading = getThreadingTestHelper(testCase);
        assertThat(threading.wasSuccessful()).isTrue();
        assertThat(threading.getSuccessResult()).isEqualTo(testCase.expectedResult());
    }

    static Stream<TestCase<?>> testDataErrors() {
        return Stream.of(
                new TestCase<>(ThreadingTests::raiseTestException, getTestException()),
                new TestCase<>(() -> null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("testDataErrors")
    <T> void testVariousErrorTypes(TestCase<T> testCase) {
        var threading = getThreadingTestHelper(testCase);
        assertThat(threading.wasSuccessful()).isFalse();
        assertThat(threading.hadError()).isTrue();
    }

    private static String raiseTestException() {
        throw getTestException();
    }

    private static RuntimeException getTestException() {
        return new RuntimeException("Test error");
    }

    private <T> ThreadDataAction<T> getThreadingTestHelper(TestCase<T> testCase) {
        ThreadDataAction<T> threadDataAction = new ThreadDataAction<>();

        var executed = ThreadMaster.action(testCase.processor)
                .callback(threadDataAction.getSuccessCallback())
                .onError(threadDataAction.getErrorCallback())
                .execute();

        await().until(executed::isCompleted);

        return threadDataAction;
    }

    record TestCase<T>(Supplier<T> processor, T expectedResult) {

        @Override
        public @NotNull String toString() {
            return expectedResult.toString();
        }
    }

    @Override
    protected void truncateData() {

    }
}