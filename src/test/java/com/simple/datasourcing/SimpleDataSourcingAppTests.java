package com.simple.datasourcing;

import com.simple.datasourcing.service.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.*;

import static com.simple.datasourcing.config.ConfigReader.getProperty;

@SpringBootTest
class SimpleDataSourcingAppTests {

    @Test
    void mongoWithTemplate() {
        String dbUrl = getProperty("mongodb.url");
        System.out.println("MongoDB URI: " + dbUrl);

        var testData1 = new TestData1("id-1-1");
        var testData1_2 = new TestData1("id-1-2");
        var testData2 = new TestData2("id-2-1-last");
        var da1 = new DataActions<>(TestData1.class);
        var da2 = new DataActions<>(TestData2.class);

        var uniqueId = "holli";

        System.out.println(da1.createFor(uniqueId, testData1));
        System.out.println(da1.createFor(uniqueId + "-next", testData1_2));
        System.out.println(da2.createFor(uniqueId, testData2));
        System.out.println(da2.deleteFor(uniqueId));

        var allFor1 = da1.getAllFor(uniqueId);
        allFor1.forEach(System.out::println);

        var lastFor2 = da2.getLastFor(uniqueId);
        System.out.println(lastFor2);
    }

    public record TestData1(String id) {
    }

    public record TestData2(String id) {
    }
}