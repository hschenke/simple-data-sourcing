package com.simple.datasourcing;

import com.simple.datasourcing.service.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
class SimpleDataSourcingAppTests extends MongoTestcontainersConfiguration {

    @Test
    void mongoWithTemplate() {
        var testData1 = new TestData1("id-1-1");
        var testData1_2 = new TestData1("id-1-2");
        var testData2 = new TestData2("id-2-1-last");

        var da1 = new DataActions<>(mongoDBContainer.getReplicaSetUrl(), TestData1.class);
        var da2 = new DataActions<>(mongoDBContainer.getReplicaSetUrl(), TestData2.class);

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