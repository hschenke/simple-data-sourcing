package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import com.simple.datasourcing.support.*;
import com.simple.datasourcing.threaded.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import static org.awaitility.Awaitility.*;

@Slf4j
class ThreadingMongoTests extends TestDataAndSetup {

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
    }

//    @AfterAll
//    static void tearDown() {
//        mongoDBContainer.stop();
//    }

    @Test
    void testThreadingMongo() {
        var dataMaster = new MongoDataMaster(mongoDBContainer.getReplicaSetUrl());
        var dataActions = dataMaster.getDataActions(TestData1.class);
        dataActions.create(uniqueId, testData1);
        var executed = ThreadMaster.action(() -> dataActions.count(uniqueId)).execute();
        await().until(executed::isCompleted);
    }

}