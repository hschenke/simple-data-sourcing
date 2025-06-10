package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import com.simple.datasourcing.support.*;
import com.simple.datasourcing.thread.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

@Slf4j
public class ThreadMongoTests extends ThreadDataSourcingTestBase {

    public ThreadMongoTests() {
        super(new MongoDataMaster(mongoDBContainer.getReplicaSetUrl()));
    }

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
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
    void miscOwnTests() {
        var booleanTDA = new ThreadDataAction<Boolean>();

        assertThreadDataAction(ThreadDataAction.constructComplete(() -> da1.create(uniqueId, testData1)), true);
        assertThreadDataAction(ThreadDataAction.constructComplete(() -> {
            da1.create(uniqueId, testData1);
            return da1.count(uniqueId);
        }), 2L);

        assertThat(da1.history().count(uniqueId)).isEqualTo(0L);

        await().until(da1.deleteInBackgroundCallback(uniqueId, booleanTDA.getSuccessCallback(), booleanTDA.getErrorCallback())::isCompleted);
        assertThat(booleanTDA.getSuccessResult()).isTrue();

        assertThreadDataAction(ThreadDataAction.constructComplete(() -> da1.count(uniqueId)), 1L);

        assertThat(da1.history().count(uniqueId)).isEqualTo(2L);

        await().until(da1.history().removeInBackgroundCallback(uniqueId, booleanTDA.getSuccessCallback(), booleanTDA.getErrorCallback())::isCompleted);
        assertThat(booleanTDA.getSuccessResult()).isTrue();

        assertThat(da1.history().count(uniqueId)).isEqualTo(0L);

        await().until(da1.history().historizationInBackgroundCallback(uniqueId, booleanTDA.getSuccessCallback(), booleanTDA.getErrorCallback())::isCompleted);
        assertThat(booleanTDA.getSuccessResult()).isTrue();

        await().until(da1.deleteInBackground(uniqueId)::isCompleted);
    }

    @Test
    void flakyTestForRealThreading() throws InterruptedException {
        assertThat(da1.count(uniqueId)).isEqualTo(0L);
        da1.create(uniqueId, testData1);
        da1.create(uniqueId, testData1);
        assertThat(da1.count(uniqueId)).isEqualTo(2L);
        da1.deleteInBackground(uniqueId);
        TimeUnit.SECONDS.sleep(3);
        assertThat(da1.count(uniqueId)).isEqualTo(1L);
    }
}