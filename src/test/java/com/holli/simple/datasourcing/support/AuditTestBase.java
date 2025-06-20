package com.holli.simple.datasourcing.support;

import lombok.extern.slf4j.*;

import java.util.*;

@SuppressWarnings("SameParameterValue")
@Slf4j
public abstract class AuditTestBase extends TestcontainerSetup implements TestDataContract {

    protected void runAuditTest() {
        setDataActions(testData1);
        checkTableNames(TestData1.class.getSimpleName().toLowerCase(), TestData1.class.getSimpleName().toLowerCase() + "_history");
        checkCreate(uniqueId, testData1);
        checkCreate(uniqueId, testData1_2);
        checkCreate(uniqueIdNext, testData1_next);
        checkCount(uniqueId, 2L);
        checkCount(uniqueIdNext, 1L);
        checkGetAllCount(3);
        checkGetAllIdsEqual(List.of(uniqueId, uniqueId, uniqueIdNext));
        checkGetAllCountEqual(uniqueId, 2, List.of(testData1_2, testData1));
        checkGetLast(uniqueId, testData1_2);
        checkIsDeleted(uniqueId, false);
        checkCountHistory(uniqueId, 0L);
        checkDelete(uniqueId);
        checkIsDeleted(uniqueId, true);
        checkGetLast(uniqueId, null);
        checkCount(uniqueId, 1L);
        checkCountHistory(uniqueId, 2L);
        checkRemove(uniqueId);
        checkCountHistory(uniqueId, 0L);
    }

    protected void runActionsFor(TestData testData) {
        setDataActions(testData);
        checkCreate(uniqueId, testData);
        checkCount(uniqueId, 1L);
        checkGetAllCountEqual(uniqueId, 1, List.of(testData));
        checkCountHistory(uniqueId, 0L);
        checkDataHistorization(uniqueId);
        checkCountHistory(uniqueId, 1L);
        checkCount(uniqueId, 0L);
    }

    protected abstract void setDataActions(TestData testData);

    protected abstract void checkTableNames(String baseName, String historyName);

    protected abstract void checkCreate(String uniqueId, TestData testData);

    protected abstract void checkCount(String uniqueId, long count);

    protected abstract void checkGetAllCount(int count);

    protected abstract void checkGetAllIdsEqual(List<String> ids);

    protected abstract void checkGetAllCountEqual(String uniqueId, int count, List<TestData> testData);

    protected abstract void checkGetLast(String uniqueId, TestData testData);

    protected abstract void checkRemove(String uniqueId);

    protected abstract void checkDelete(String uniqueId);

    protected abstract void checkCountHistory(String uniqueId, long count);

    protected abstract void checkIsDeleted(String uniqueId, boolean isDeleted);

    protected abstract void checkDataHistorization(String uniqueId);
}