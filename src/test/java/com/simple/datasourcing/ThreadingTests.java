package com.simple.datasourcing;

import com.simple.datasourcing.model.*;
import com.simple.datasourcing.support.*;
import com.simple.datasourcing.threaded.*;
import org.jetbrains.annotations.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

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

    private <T> @NotNull ThreadingTestHelper<T> getThreadingTestHelper(TestCase<T> testCase) {
        var helper = new ThreadingTestHelper<T>();

        ThreadMaster.action(testCase.processor)
                .callback(helper.getSuccessCallback())
                .onError(helper.getErrorCallback())
                .execute();

//        ThreadMaster.get().virtualWithCallback(
//                testCase.processor(),
//                helper.getSuccessCallback(),
//                helper.getErrorCallback()
//        );

        helper.awaitCompletion(Duration.ofSeconds(5));
        return helper;
    }

    record TestCase<T>(Supplier<T> processor, T expectedResult) {
        @Override
        public @NotNull String toString() {
            return expectedResult.toString();
        }
    }
}