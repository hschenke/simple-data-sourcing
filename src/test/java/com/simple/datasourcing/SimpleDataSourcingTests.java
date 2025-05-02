package com.simple.datasourcing;

import com.simple.datasourcing.contracts.*;
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
        dataMaster = MongoDataMaster.get(mongoDBContainer.getReplicaSetUrl());
        uniqueId = "holli";
        uniqueIdNext = "holli-next";
    }

    TestData1 testData1;
    TestData1 testData1_2;
    TestData1 testData1_next;
    TestData2 testData2;

    MongoDataMaster.MongoActions<TestData1> actions1;
    MongoDataActions<TestData1> da1;
    MongoDataActions<TestData1>.History da1H;
    DataMaster.Actions.AllActions<TestData2> da2All;

    @BeforeEach
    void beforeEach() {
        testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
        testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
        testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
        testData2 = new TestData2("id-2-1", List.of(testData1, testData1_2));

        actions1 = dataMaster.actionsFor(TestData1.class);
        da1 = actions1.getBase();
        da1H = actions1.getHistory();
        da2All = dataMaster.actionsFor(TestData2.class).getAll();
    }

    @AfterEach
    void afterEach() {
        da1.truncate();
        da1H.truncate();
        da2All.actionsBase().truncate();
        da2All.actionsHistory().truncate();
    }

    @Test
    void dataMasterTest() {
        assertThat(da1.getTableName()).isEqualTo(TestData1.class.getSimpleName());
        assertThat(da1H.getTableName()).isEqualTo(TestData1.class.getSimpleName().concat("-history"));

        assertThat(da1.createFor(uniqueId, testData1)).isNotNull();
        assertThat(da1.createFor(uniqueId, testData1_2)).isNotNull();
        assertThat(da1.createFor(uniqueIdNext, testData1_next)).isNotNull();
        assertThat(actions1.getDataActions().getDataTemplate().count(new Query(), actions1.getBase().getTableName())).isEqualTo(3);
        assertThat(da1.countFor(uniqueId)).isEqualTo(2);
        assertThat(da1.getAllFor(uniqueId)).hasSize(2).isEqualTo(List.of(testData1, testData1_2));
        assertThat(da1.getLastFor(uniqueId)).isEqualTo(testData1_2);

        assertThat(da1.isDeleted(uniqueId)).isFalse();
        assertThat(da1H.countFor(uniqueId)).isEqualTo(0);
        assertThat(da1.deleteFor(uniqueId)).isNotNull();
        assertThat(da1.isDeleted(uniqueId)).isTrue();
        assertThat(da1.getLastFor(uniqueId)).isNull();
        assertThat(da1H.countFor(uniqueId)).isEqualTo(2);

        assertThat(da1H.dataHistorization(uniqueId, true)).isTrue();
        assertThat(da1H.getAllFor(uniqueId)).hasSize(3);
        assertThat(da1H.countFor(uniqueId)).isEqualTo(3);
        assertThat(actions1.getDataActions().getDataTemplate().count(new Query(), actions1.getBase().getTableName())).isEqualTo(1);
        assertThat(da1H.removeFor(uniqueId)).isTrue();
        assertThat(da1H.countFor(uniqueId)).isEqualTo(0);
    }

    @Test
    void dataAllActionsTest() {
        assertThat(da2All.actionsBase().createFor(uniqueId, testData2)).isNotNull();
        assertThat(da2All.actionsBase().countFor(uniqueId)).isEqualTo(1);
        assertThat(da2All.actionsHistory().countFor(uniqueId)).isEqualTo(0);
        assertThat(da2All.actionsHistory().dataHistorization(uniqueId, true)).isTrue();
        assertThat(da2All.actionsHistory().countFor(uniqueId)).isEqualTo(1);
        assertThat(da2All.actionsBase().countFor(uniqueId)).isEqualTo(0);
    }

    @Test
    void tableExistsTest() {
        assertThat(da1.getService().bothTablesExists()).isTrue();

        dataMaster.getDbTemplate().dropCollection(da1.getTableName());
        dataMaster.getDbTemplate().dropCollection(da1H.getTableName());
        assertThat(da1.getService().bothTablesExists()).isFalse();
    }

    public record TestData1(String id, String name, Object data) {
    }

    public record TestData2(String id, List<TestData1> testData1s) {
    }
}