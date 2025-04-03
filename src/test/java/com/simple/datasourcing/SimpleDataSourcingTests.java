package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.springframework.data.mongodb.core.query.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class SimpleDataSourcingTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    static MongoDataMaster dataMaster;
    static String uniqueId;
    static String uniqueIdNext;

    @BeforeAll
    static void beforeAll() {
        mongoDBContainer.start();
        dataMaster = new MongoDataMaster(mongoDBContainer.getReplicaSetUrl());
        uniqueId = "holli";
        uniqueIdNext = "holli-next";
    }

    TestData1 testData1;
    TestData1 testData1_2;
    TestData1 testData1_next;
    TestData2 testData2;
    TestData3 testData3;

    MongoDataActions<TestData1> da1;
    MongoDataActionsHistory<TestData1> da1H;
    MongoDataActions<TestData2> da2;
    MongoDataActions<TestData3> da3;

    @BeforeEach
    void beforeEach() {
        testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
        testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
        testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
        testData2 = new TestData2("id-2-1-last", "name-2-1", 2.1);
        testData3 = new TestData3("id-3-1", List.of(testData1, testData1_2));

        da1 = dataMaster.getDataActions(TestData1.class);
        da1H = dataMaster.getDataActionsHistory(TestData1.class);

        da2 = new MongoDataActions<>(dataMaster.getDataTemplate(), TestData2.class);
        da3 = new MongoDataActions<>(dataMaster.getDataTemplate(), TestData3.class);
    }

    @AfterEach
    void afterEach() {
        da1.truncate();
        da1H.truncate();
        da2.truncate();
        da3.truncate();
    }

    @Test
    void dataMasterTest() {
        assertThat(da1.getTableName()).isEqualTo(TestData1.class.getSimpleName());
        assertThat(da1H.getTableName()).isEqualTo(da1H.getTableNameHistory());

        assertThat(da1.createFor(uniqueId, testData1)).isNotNull();
        assertThat(da1.createFor(uniqueId, testData1_2)).isNotNull();
        assertThat(da1.createFor(uniqueIdNext, testData1_next)).isNotNull();
        assertThat(dataMaster.getDataTemplate().count(new Query(), TestData1.class.getSimpleName())).isEqualTo(3);
        assertThat(da1.countFor(uniqueId)).isEqualTo(2);
        assertThat(da1.getAllFor(uniqueId)).hasSize(2).isEqualTo(List.of(testData1, testData1_2));
        assertThat(da1.getLastFor(uniqueId)).isEqualTo(testData1_2);
        assertThat(da1.isDeleted(uniqueId)).isFalse();
        assertThat(da1.deleteFor(uniqueId)).isNotNull();
        assertThat(da1.isDeleted(uniqueId)).isTrue();
        assertThat(da1.countFor(uniqueId)).isEqualTo(0);
        assertThat(da1.getAllFor(uniqueId)).isEmpty();
        assertThat(da1.getLastFor(uniqueId)).isNull();

        assertThat(da1H.countFor(uniqueId)).isEqualTo(0);
        assertThat(da1H.getAllFor(uniqueId)).isEmpty();
        assertThat(da1H.doFullHistory(uniqueId)).isTrue();
        assertThat(da1H.getAllFor(uniqueId)).hasSize(3);
        assertThat(da1H.countFor(uniqueId)).isEqualTo(3);
        assertThat(dataMaster.getDataTemplate().count(new Query(), TestData1.class.getSimpleName())).isEqualTo(1);
        assertThat(da1H.removeFor(uniqueId)).isTrue();
        assertThat(da1H.countFor(uniqueId)).isEqualTo(0);
    }

    @Test
    void dataTests() {
        logLineAndDebug(da2.createFor(uniqueId, testData2));
        logDebug(da2.deleteFor(uniqueId));

        logLineAndDebug(da3.createFor(uniqueId, testData3));
        assertThat(da3.getLastFor(uniqueId).testData1s()).hasSize(2).contains(testData1, testData1_2);

        logLineAndDebug(da2.getLastFor(uniqueId));
        logLineAndDebug(da3.getLastFor(uniqueId));
        logDebugLine();

        assertThat(da2.getAllFor(uniqueId)).isEmpty();
        assertThat(da2.isDeleted(uniqueId)).isTrue();
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

    public record TestData3(String id, List<TestData1> testData1s) {
    }
}