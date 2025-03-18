package com.simple.datasourcing;

import com.simple.datasourcing.service.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class SimpleDataSourcingTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    static {
        mongoDBContainer.start();
    }

    @Test
    void dataTests() {
        var testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
        var testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
        var testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
        var testData2 = new TestData2("id-2-1-last", "name-2-1", 2.1);

        var da1 = new DataActions<>(mongoDBContainer.getReplicaSetUrl(), TestData1.class);
        var da2 = new DataActions<>(mongoDBContainer.getReplicaSetUrl(), TestData2.class);

        var uniqueId = "holli";

        logLineAndDebug(da1.createFor(uniqueId, testData1));
        logDebug(da1.createFor(uniqueId, testData1_2));
        logDebug(da1.createFor(uniqueId + "-next", testData1_next));

        logLineAndDebug(da2.createFor(uniqueId, testData2));
        logDebug(da2.deleteFor(uniqueId));

        logDebugLine();
        var allFor1 = da1.getAllFor(uniqueId);
        allFor1.forEach(this::logDebug);

        var lastFor1 = da1.getLastFor(uniqueId);
        logLineAndDebug(lastFor1);

        var lastFor2 = da2.getLastFor(uniqueId);
        logLineAndDebug(lastFor2);
        logDebugLine();

        assertThat(da1.isDeleted(uniqueId)).isFalse();
        assertThat(da2.isDeleted(uniqueId)).isTrue();

        assertThat(da1.countFor(uniqueId)).isEqualTo(2);
        assertThat(da2.countFor(uniqueId)).isEqualTo(0);
    }

    private void logLineAndDebug(Object o) {
        logDebugLine();
        logDebug(o);
    }

    private void logDebug(Object o) {
        log.info("{}", o);
    }

    private void logDebugLine() {
        logDebug("-".repeat(100));
    }

    public record TestData1(String id, String name, Object data) {
    }

    public record TestData2(String id, String name, Object data) {
    }
}