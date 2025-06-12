package com.simple.datasourcing.support;

import com.simple.datasourcing.contracts.master.*;
import com.simple.datasourcing.thread.*;
import lombok.extern.slf4j.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

@Slf4j
public class ThreadDataSourcingTestBase extends DataSourcingTestBase {

    public ThreadDataSourcingTestBase(DataMaster dataMaster) {
        super(dataMaster);
    }

    @Override
    protected void checkTableNames(String baseName, String historyName) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.getTableName()), baseName);
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActionsHistory.getTableName()), historyName);
    }

    @Override
    protected void checkCreate(String uniqueId, TestData testData) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> getDa(testData).add(uniqueId, testData)), true);
    }

    @Override
    protected void checkCount(String uniqueId, long count) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.count(uniqueId)), count);
    }

    @Override
    protected void checkGetAllCountEqual(String uniqueId, int count, List<TestData> testDataList) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.getAll(uniqueId)), testDataList);
    }

    @Override
    protected void checkGetLast(String uniqueId, TestData testData) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.getLast(uniqueId)), testData);
    }

    @Override
    protected void checkRemove(String uniqueId) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActionsHistory.remove(uniqueId)), true);
    }

    @Override
    protected void checkDelete(String uniqueId) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.delete(uniqueId)), true);
    }

    @Override
    protected void checkCountHistory(String uniqueId, long count) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActionsHistory.count(uniqueId)), count);
    }

    @Override
    protected void checkIsDeleted(String uniqueId, boolean isDeleted) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.isDeleted(uniqueId)), isDeleted);
    }

    @Override
    protected void checkDataHistorization(String uniqueId) {
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActionsHistory.historization(uniqueId)), true);
    }

    public <T> void assertThreadDataAction(ThreadDataAction<T> threadDataAction, T expected) {
        await().until(threadDataAction::isCompleted);
        assertThat(threadDataAction.getSuccessResult()).isEqualTo(expected);
    }
}