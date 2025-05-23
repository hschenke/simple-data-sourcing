package com.simple.datasourcing;

import com.simple.datasourcing.contracts.*;
import lombok.extern.slf4j.*;

import java.util.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class DataSourcingTestBase extends TestData {

    DataActions<TestData1> da1;
    DataActions<TestData1>.History da1History;
    DataActions<TestData2> da2;

    public DataSourcingTestBase(DataActions<TestData1> da1, DataActions<TestData2> da2) {
        this.da1 = da1;
        this.da1History = da1.history();
        this.da2 = da2;
    }

    void truncateData() {
        da1.truncate();
        da1History.truncate();
        da2.truncate();
        da2.history().truncate();
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
        truncateData();
        assertThat(da2.createFor(uniqueId, testData2)).isNotNull();
        assertThat(da2.countFor(uniqueId)).isEqualTo(1);
        assertThat(da2.history().countFor(uniqueId)).isEqualTo(0);
        assertThat(da2.history().dataHistorization(uniqueId)).isTrue();
        assertThat(da2.history().countFor(uniqueId)).isEqualTo(1);
        assertThat(da2.countFor(uniqueId)).isEqualTo(0);
    }

    void tableExistsTest(DataService<?, ?, ?> service, Consumer<String> dropTable) {
        assertThat(service.tableExists(service.getTableNameBase())).isTrue();
        assertThat(service.tableExists(service.getTableNameHistory())).isTrue();

        dropTable.accept(da1.getTableName());
        dropTable.accept(da1History.getTableName());

        assertThat(service.tableExists(service.getTableNameBase())).isFalse();
        assertThat(service.tableExists(service.getTableNameHistory())).isFalse();
    }
}