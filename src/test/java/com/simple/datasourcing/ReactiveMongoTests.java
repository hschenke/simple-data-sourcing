package com.simple.datasourcing;

import com.simple.datasourcing.mongo.reactive.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

@Slf4j
class ReactiveMongoTests extends ReactiveDataSourcingTestBase {

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void tearDown() {
        mongoDBContainer.stop();
    }

    public ReactiveMongoTests() {
        super(new ReactiveMongoDataMaster(mongoDBContainer.getReplicaSetUrl()));
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
}