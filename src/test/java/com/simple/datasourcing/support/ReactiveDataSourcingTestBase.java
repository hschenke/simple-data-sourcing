package com.simple.datasourcing.support;

import com.simple.datasourcing.contracts.reactive.*;
import lombok.extern.slf4j.*;
import reactor.test.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ReactiveDataSourcingTestBase extends TestBase {

    ReactiveDataActions<?> dataActions;
    ReactiveDataActions<?>.History dataActionsHistory;

    ReactiveDataActions<TestData1> da1;
    ReactiveDataActions<TestData2> da2;
    ReactiveDataActions<TestData3> da3;

    public ReactiveDataSourcingTestBase(ReactiveDataMaster dataMaster) {
        this.da1 = dataMaster.getDataActions(TestData1.class);
        this.da2 = dataMaster.getDataActions(TestData2.class);
        this.da3 = dataMaster.getDataActions(TestData3.class);
    }

    protected void truncateData() {
        da1.truncate().subscribe();
        da1.history().truncate().subscribe();
        da2.truncate().subscribe();
        da2.history().truncate().subscribe();
        da3.truncate().subscribe();
        da3.history().truncate().subscribe();
    }

    @Override
    protected void setDataActions(TestData testData) {
        this.dataActions = getDa(testData);
        this.dataActionsHistory = getDaHistory(testData);
    }

    @SuppressWarnings("unchecked")
    <T extends TestData> ReactiveDataActions<T> getDa(TestData testData) {
        return (ReactiveDataActions<T>) switch (testData) {
            case TestData1 ignored -> da1;
            case TestData2 ignored -> da2;
            case TestData3 ignored -> da3;
        };
    }

    @SuppressWarnings("unchecked")
    <T extends TestData> ReactiveDataActions<T>.History getDaHistory(TestData testData) {
        return (ReactiveDataActions<T>.History) switch (testData) {
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
        StepVerifier.create(getDa(testData).create(uniqueId, testData)).expectNext(true).verifyComplete();
    }

    @Override
    protected void checkCount(String uniqueId, long count) {
        StepVerifier.create(dataActions.count(uniqueId)).expectNext(count).verifyComplete();
    }

    @Override
    protected void checkGetAllEqual(String uniqueId, int count, List<TestData> testDataList) {
        StepVerifier.FirstStep<?> getAll = StepVerifier.create(dataActions.getAll(uniqueId));
        testDataList.forEach(_ -> getAll.expectNextMatches(Objects::nonNull));
        getAll.verifyComplete();
    }

    @Override
    protected void checkGetLast(String uniqueId, TestData testData) {
        if (testData == null)
            StepVerifier.create(dataActions.getLast(uniqueId).log()).verifyComplete();
        else
            StepVerifier.create(dataActions.getLast(uniqueId).log()).expectNextMatches(Objects::nonNull).verifyComplete();
    }

    @Override
    protected void checkRemove(String uniqueId) {
        StepVerifier.create(dataActionsHistory.remove(uniqueId)).expectNext(true).verifyComplete();
    }

    @Override
    protected void checkDelete(String uniqueId) {
        StepVerifier.create(dataActions.delete(uniqueId)).expectNext(true).verifyComplete();
    }

    @Override
    protected void checkCountHistory(String uniqueId, long count) {
        StepVerifier.create(dataActionsHistory.count(uniqueId)).expectNext(count).verifyComplete();
    }

    @Override
    protected void checkIsDeleted(String uniqueId, boolean isDeleted) {
        StepVerifier.create(dataActions.isDeleted(uniqueId)).expectNext(isDeleted).verifyComplete();
    }

    @Override
    protected void checkDataHistorization(String uniqueId) {
        StepVerifier.create(dataActionsHistory.historization(uniqueId)).expectNext(true).verifyComplete();
    }
}