package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

@Slf4j
class MongoTests extends DataSourcingTestBase {

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void tearDown() {
        mongoDBContainer.stop();
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
}