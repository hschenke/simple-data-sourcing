package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
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

    MongoDataActions<TestData1> da1;
    MongoDataActions<TestData1>.History da1History;

    @BeforeEach
    void beforeEach() {
        testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
        testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
        testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
        testData2 = new TestData2("id-2-1", List.of(testData1, testData1_2));

        da1 = dataMaster.getDataActions(TestData1.class);
        da1History = da1.new History();
    }

    @AfterEach
    void afterEach() {
        da1.truncate();
        da1History.truncate();
    }

    @Test
    void dataMasterTest() {
        assertThat(da1.getTableName()).isEqualTo(TestData1.class.getSimpleName());
        assertThat(da1History.getTableName()).isEqualTo(TestData1.class.getSimpleName().concat("-history"));

        assertThat(da1.createFor(uniqueId, testData1)).isNotNull();
        assertThat(da1.createFor(uniqueId, testData1_2)).isNotNull();
        assertThat(da1.createFor(uniqueIdNext, testData1_next)).isNotNull();
        assertThat(da1.countFor(uniqueId)).isEqualTo(2);
        assertThat(da1.getAllFor(uniqueId)).hasSize(2).isEqualTo(List.of(testData1, testData1_2));
        assertThat(da1.getLastFor(uniqueId)).isEqualTo(testData1_2);

        assertThat(da1.isDeleted(uniqueId)).isFalse();
        assertThat(da1History.countFor(uniqueId)).isEqualTo(0);
        assertThat(da1.deleteFor(uniqueId)).isNotNull();
        assertThat(da1.isDeleted(uniqueId)).isTrue();
        assertThat(da1.getLastFor(uniqueId)).isNull();
        assertThat(da1History.countFor(uniqueId)).isEqualTo(2);

        assertThat(da1History.dataHistorization(uniqueId, true)).isTrue();
        assertThat(da1History.getAllFor(uniqueId)).hasSize(3);
        assertThat(da1History.countFor(uniqueId)).isEqualTo(3);
        assertThat(da1History.removeFor(uniqueId)).isTrue();
        assertThat(da1History.countFor(uniqueId)).isEqualTo(0);
    }

    @Test
    void dataAllActionsTest() {
        var da2 = dataMaster.getDataActions(TestData2.class);
        assertThat(da2.createFor(uniqueId, testData2)).isNotNull();
        assertThat(da2.countFor(uniqueId)).isEqualTo(1);
        assertThat(da2.history().countFor(uniqueId)).isEqualTo(0);
        assertThat(da2.history().dataHistorization(uniqueId, true)).isTrue();
        assertThat(da2.history().countFor(uniqueId)).isEqualTo(1);
        assertThat(da2.countFor(uniqueId)).isEqualTo(0);
    }

    @Test
    void tableExistsTest() {
        assertThat(da1.getService().bothTablesExists()).isTrue();

        da1.getService().dataTemplate().dropCollection(da1.getTableName());
        da1.getService().dataTemplate().dropCollection(da1History.getTableName());
        assertThat(da1.getService().bothTablesExists()).isFalse();
    }

    public record TestData1(String id, String name, Object data) {
    }

    public record TestData2(String id, List<TestData1> testData1s) {
    }
}