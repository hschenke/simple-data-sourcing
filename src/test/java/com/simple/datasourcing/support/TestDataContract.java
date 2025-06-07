package com.simple.datasourcing.support;

import java.time.ZonedDateTime;
import java.util.List;

public interface TestDataContract {

    String uniqueId = "holli";
    String uniqueIdNext = "holli-next";

    TestData1 testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
    TestData1 testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
    TestData1 testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
    TestData2 testData2 = new TestData2("id-2-1", List.of(testData1, testData1_2));
    TestData3 testData3 = new TestData3("id-3-1", ZonedDateTime.now());

    sealed interface TestData permits TestData1, TestData2, TestData3 {
    }

    record TestData1(String id, String name, Object data) implements TestData {
    }

    record TestData2(String id, List<TestData1> testData1s) implements TestData {
    }

    record TestData3(String id, ZonedDateTime dateTime) implements TestData {
    }
}