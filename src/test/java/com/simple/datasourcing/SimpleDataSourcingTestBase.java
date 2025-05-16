package com.simple.datasourcing;

import com.simple.datasourcing.contracts.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class SimpleDataSourcingTestBase {

    static String uniqueId = "holli";
    static String uniqueIdNext = "holli-next";

    TestData1 testData1;
    TestData1 testData1_2;
    TestData1 testData1_next;
    TestData2 testData2;

    DataActions<TestData1> da1;
    DataActions<TestData1>.History da1History;
    DataActions<TestData2> da2;

    public SimpleDataSourcingTestBase(DataActions<TestData1> da1, DataActions<TestData2> da2) {
        this.da1 = da1;
        this.da1History = da1.history();
        this.da2 = da2;
    }

    @BeforeEach
    void beforeEach() {
        testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
        testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
        testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
        testData2 = new TestData2("id-2-1", List.of(testData1, testData1_2));
    }

    void truncateData() {
        da1.truncate();
        da1History.truncate();
    }

    void dataMasterTest() {
        assertThat(da1.getTableName()).isEqualTo(TestData1.class.getSimpleName().toLowerCase());
        assertThat(da1History.getTableName()).isEqualTo(TestData1.class.getSimpleName().concat("_history").toLowerCase());

        assertThat(da1.createFor(uniqueId, testData1)).isNotNull();
        assertThat(da1.createFor(uniqueId, testData1_2)).isNotNull();
        assertThat(da1.createFor(uniqueIdNext, testData1_next)).isNotNull();
        assertThat(da1.countFor(uniqueId)).isEqualTo(2);
        assertThat(da1.getAllFor(uniqueId)).hasSize(2).isEqualTo(List.of(testData1, testData1_2));
        assertThat(da1.getLastFor(uniqueId)).isEqualTo(testData1_2);

        assertThat(da1.isDeleted(uniqueId)).isFalse();
        assertThat(da1History.countFor(uniqueId)).isEqualTo(0);
        assertThat(da1.deleteFor(uniqueId)).isTrue();
        assertThat(da1.isDeleted(uniqueId)).isTrue();
        assertThat(da1.getLastFor(uniqueId)).isNull();
        assertThat(da1.countFor(uniqueId)).isEqualTo(1);
        assertThat(da1History.countFor(uniqueId)).isEqualTo(2);

        assertThat(da1History.dataHistorization(uniqueId)).isTrue();
        assertThat(da1History.getAllFor(uniqueId)).hasSize(3);
        assertThat(da1History.countFor(uniqueId)).isEqualTo(3);
        assertThat(da1History.removeFor(uniqueId)).isTrue();
        assertThat(da1History.countFor(uniqueId)).isEqualTo(0);

        truncateData();
    }


    void dataAllActionsTest() {
        assertThat(da2.createFor(uniqueId, testData2)).isNotNull();
        assertThat(da2.countFor(uniqueId)).isEqualTo(1);
        assertThat(da2.history().countFor(uniqueId)).isEqualTo(0);
        assertThat(da2.history().dataHistorization(uniqueId)).isTrue();
        assertThat(da2.history().countFor(uniqueId)).isEqualTo(1);
        assertThat(da2.countFor(uniqueId)).isEqualTo(0);

        truncateData();
    }

    void tableExistsTest(DataService<?, ?, ?> service, Consumer<String> dropTable) {
        assertThat(service.bothTablesExists()).isTrue();

        dropTable.accept(da1.getTableName());
        dropTable.accept(da1History.getTableName());

        assertThat(service.bothTablesExists()).isFalse();
    }

    public record TestData1(String id, String name, Object data) {
    }

    public record TestData2(String id, List<TestData1> testData1s) {
    }
}