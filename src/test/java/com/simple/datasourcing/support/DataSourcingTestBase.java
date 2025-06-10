package com.simple.datasourcing.support;

import com.simple.datasourcing.contracts.actions.*;
import com.simple.datasourcing.contracts.master.*;
import lombok.extern.slf4j.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class DataSourcingTestBase extends AuditTestBase {

    public DataActions<?> dataActions;
    public DataActions<?>.History dataActionsHistory;

    public DataActions<TestData1> da1;
    public DataActions<TestData2> da2;
    public DataActions<TestData3> da3;

    public DataSourcingTestBase(DataMaster dataMaster) {
        this.da1 = dataMaster.getDataActions(TestData1.class);
        this.da2 = dataMaster.getDataActions(TestData2.class);
        this.da3 = dataMaster.getDataActions(TestData3.class);
    }

    protected void truncateData() {
        da1.truncate();
        da1.history().truncate();
        da2.truncate();
        da2.history().truncate();
        da3.truncate();
        da3.history().truncate();
    }

    @Override
    protected void setDataActions(TestData testData) {
        this.dataActions = getDa(testData);
        this.dataActionsHistory = getDaHistory(testData);
    }

    @SuppressWarnings("unchecked")
    <T extends TestData> DataActions<T> getDa(TestData testData) {
        return (DataActions<T>) switch (testData) {
            case TestData1 ignored -> da1;
            case TestData2 ignored -> da2;
            case TestData3 ignored -> da3;
        };
    }

    @SuppressWarnings("unchecked")
    <T extends TestData> DataActions<T>.History getDaHistory(TestData testData) {
        return (DataActions<T>.History) switch (testData) {
            case TestData1 ignored -> da1.history();
            case TestData2 ignored -> da2.history();
            case TestData3 ignored -> da3.history();
        };
    }

    @Override
    protected void checkTableNames(String baseName, String historyName) {
        assertThat(dataActions.getTableName()).isEqualTo(baseName);
        assertThat(dataActionsHistory.getTableName()).isEqualTo(historyName);
    }

    @Override
    protected void checkCreate(String uniqueId, TestData testData) {
        assertThat(getDa(testData).create(uniqueId, testData)).isTrue();
    }

    @Override
    protected void checkCount(String uniqueId, long count) {
        assertThat(dataActions.count(uniqueId)).isEqualTo(count);
    }

    @Override
    protected void checkGetAllCount(int count) {
        assertThat(dataActions.getAll()).hasSize(count);
    }

    @Override
    protected void checkGetAllIdsEqual(List<String> ids) {
        assertThat(dataActions.getAllIds()).isEqualTo(ids);
    }

    @Override
    protected void checkGetAllCountEqual(String uniqueId, int count, List<TestData> testDataList) {
        assertThat(dataActions.getAll(uniqueId)).hasSize(count).isEqualTo(testDataList);
    }

    @Override
    protected void checkGetLast(String uniqueId, TestData testData) {
        assertThat(dataActions.getLast(uniqueId)).isEqualTo(testData);
    }

    @Override
    protected void checkRemove(String uniqueId) {
        assertThat(dataActionsHistory.remove(uniqueId)).isTrue();
    }

    @Override
    protected void checkDelete(String uniqueId) {
        assertThat(dataActions.delete(uniqueId)).isTrue();
    }

    @Override
    protected void checkCountHistory(String uniqueId, long count) {
        assertThat(dataActionsHistory.count(uniqueId)).isEqualTo(count);
    }

    @Override
    protected void checkIsDeleted(String uniqueId, boolean isDeleted) {
        assertThat(dataActions.isDeleted(uniqueId)).isEqualTo(isDeleted);
    }

    @Override
    protected void checkDataHistorization(String uniqueId) {
        assertThat(dataActionsHistory.historization(uniqueId)).isTrue();
    }
}