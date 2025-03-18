package com.simple.datasourcing;

import com.simple.datasourcing.service.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

@Slf4j
class SimpleDataSourcingTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    static {
        mongoDBContainer.start();
    }

    @Test
    void mongoWithTemplate() {
        var testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
        var testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
        var testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
        var testData2 = new TestData2("id-2-1-last", "name-2-1", 2.1);

        var da1 = new DataActions<>(mongoDBContainer.getReplicaSetUrl(), TestData1.class);
        var da2 = new DataActions<>(mongoDBContainer.getReplicaSetUrl(), TestData2.class);

        var uniqueId = "holli";

        logDebug(da1.createFor(uniqueId, testData1));
        logDebug(da1.createFor(uniqueId, testData1_2));
        logDebug(da1.createFor(uniqueId + "-next", testData1_next));
        logDebugLine();

        logDebug(da2.createFor(uniqueId, testData2));
        logDebug(da2.deleteFor(uniqueId));
        logDebugLine();

        var allFor1 = da1.getAllFor(uniqueId);
        allFor1.forEach(this::logDebug);
        logDebugLine();

        var lastFor1 = da1.getLastFor(uniqueId);
        logDebugAndLine(lastFor1);

        logDebugAndLine(da1.countFor(uniqueId));

        var lastFor2 = da2.getLastFor(uniqueId);
        logDebugAndLine(lastFor2);
    }

    private void logDebugAndLine(Object o) {
        logDebug(o);
        logDebugLine();
    }

    private void logDebug(Object o) {
        log.info("{}", o);
    }

    private void logDebugLine() {
        logDebug("-".repeat(50));
    }

    public record TestData1(String id, String name, Object data) {
    }

    public record TestData2(String id, String name, Object data) {
    }
}