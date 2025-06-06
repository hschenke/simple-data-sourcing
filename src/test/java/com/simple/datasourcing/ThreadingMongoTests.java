package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import com.simple.datasourcing.support.*;
import com.simple.datasourcing.thread.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

@Slf4j
public class ThreadingMongoTests extends TestDataAndSetup {

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
    }

    MongoDataMaster dataMaster;
    MongoDataActions<TestData1> dataActions;

    protected void truncateData() {
        dataMaster = new MongoDataMaster(mongoDBContainer.getReplicaSetUrl());
        dataActions = dataMaster.getDataActions(TestData1.class);
        assertThat(dataActions.truncate()).isTrue();
        assertThat(dataActions.history().truncate()).isTrue();
    }

    @Test
    void testThreadingMongo() {
        var booleanTDA = new ThreadDataAction<Boolean>();

        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.create(uniqueId, testData1)), true);
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> {
            dataActions.create(uniqueId, testData1);
            return dataActions.count(uniqueId);
        }), 2L);

        assertThat(dataActions.history().count(uniqueId)).isEqualTo(0L);

        await().until(dataActions.deleteInBackgroundCallback(uniqueId, booleanTDA.getSuccessCallback(), booleanTDA.getErrorCallback())::isCompleted);
        assertThat(booleanTDA.getSuccessResult()).isTrue();

        assertThreadDataAction(ThreadDataAction.constructComplete(() -> dataActions.count(uniqueId)), 1L);

        assertThat(dataActions.history().count(uniqueId)).isEqualTo(2L);

        await().until(dataActions.history().removeInBackgroundCallback(uniqueId, booleanTDA.getSuccessCallback(), booleanTDA.getErrorCallback())::isCompleted);
        assertThat(booleanTDA.getSuccessResult()).isTrue();

        assertThat(dataActions.history().count(uniqueId)).isEqualTo(0L);

        await().until(dataActions.history().historizationInBackgroundCallback(uniqueId, booleanTDA.getSuccessCallback(), booleanTDA.getErrorCallback())::isCompleted);
        assertThat(booleanTDA.getSuccessResult()).isTrue();

        await().until(dataActions.deleteInBackground(uniqueId)::isCompleted);
    }

    private <T> void assertThreadDataAction(ThreadDataAction<T> threadDataAction, T expected) {
        await().until(threadDataAction::isCompleted);
        assertThat(threadDataAction.getSuccessResult()).isEqualTo(expected);
    }
}