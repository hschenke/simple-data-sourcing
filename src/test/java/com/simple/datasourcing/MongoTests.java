package com.simple.datasourcing;

import com.simple.datasourcing.contracts.actions.*;
import com.simple.datasourcing.mongo.*;
import com.simple.datasourcing.support.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import static org.awaitility.Awaitility.await;

@Slf4j
class MongoTests extends DataSourcingTestBase {

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void tearDown() {
        if (mongoDBContainer.isHostAccessible()) mongoDBContainer.stop();
    }

    public MongoTests() {
        super(new MongoDataMaster(mongoDBContainer.getReplicaSetUrl()));
    }

    @Test
    void audit() {
        runAuditTest();
    }

    @Test
    void allActions1() {
        runActionsFor(testData1);
    }

    @Test
    void allActions2() {
        runActionsFor(testData2);
    }

    @Test
    void allActions3() {
        runActionsFor(testData3);
    }

    @Test
    void deleteAll() {
        prepareData(da1, testData1);
        prepareData(da2, testData2);
        prepareData(da3, testData3);

        dataMaster.deleteAll(uniqueId, da1, da2, da3);

        checkCount(da1, 1);
        checkCount(da2, 1);
        checkCount(da3, 1);
    }

    @Test
    void deleteAllInBackground() {
        prepareData(da1, testData1);
        prepareData(da2, testData2);
        prepareData(da3, testData3);

        await().until( dataMaster.deleteAllInBackground(uniqueId, da1, da2, da3)::isCompleted);

        checkCount(da1, 1);
        checkCount(da2, 1);
        checkCount(da3, 1);
    }

    @SuppressWarnings("unchecked")
    private <T> void prepareData(DataActions<T> actions, TestData data) {
        checkCount(actions, 0);
        actions.add(uniqueId, (T) data);
        actions.add(uniqueId, (T) data);
        checkCount(actions, 2);
    }
}