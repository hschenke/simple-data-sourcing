package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import com.simple.datasourcing.support.*;
import com.simple.datasourcing.threaded.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.function.*;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

@Slf4j
class ThreadingMongoTests extends TestDataAndSetup {

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
    }

    @Test
    void testThreadingMongo() {
        var dataMaster = new MongoDataMaster(mongoDBContainer.getReplicaSetUrl());
        var dataActions = dataMaster.getDataActions(TestData1.class);

        dataActions.truncate();
        executeAndProof(() -> dataActions.create(uniqueId, testData1), true);
        executeAndProof(() -> {
            dataActions.create(uniqueId, testData1);
            return dataActions.count(uniqueId);
        }, 2L);
        assertThat(dataActions.history().count(uniqueId)).isEqualTo(0L);
        await().until(dataActions.deleteInBackground(uniqueId)::isCompleted);
        executeAndProof(() -> dataActions.count(uniqueId), 1L);
        assertThat(dataActions.history().count(uniqueId)).isEqualTo(2L);
    }

    private <T> void executeAndProof(Supplier<T> supplier, T expected) {
        var executed = ThreadMaster
                .action(supplier)
                .callback(returnValue -> callbackHandler(returnValue, expected))
                .execute();

        await().until(executed::isCompleted);
    }

    private <T> void callbackHandler(T returnValue, T expected) {
        log.info("check returnValue {} == {}", returnValue, expected);
        assertThat(returnValue).isEqualTo(expected);
    }
}