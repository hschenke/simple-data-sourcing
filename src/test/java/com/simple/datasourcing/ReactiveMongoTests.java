package com.simple.datasourcing;

import com.simple.datasourcing.mongo.reactive.*;
import com.simple.datasourcing.support.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import reactor.core.publisher.*;
import reactor.test.*;

import java.util.*;

@Slf4j
class ReactiveMongoTests extends ReactiveDataSourcingTestBase {

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
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

    @Test
    void deleteAll() {
        checkCount(da1, 0);
        checkCreate(uniqueId, testData1);
        checkCreate(uniqueId, testData1);
        checkCount(da1, 2);
        checkCount(da2, 0);
        checkCreate(uniqueId, testData2);
        checkCreate(uniqueId, testData2);
        checkCount(da2, 2);

        StepVerifier.create(dataMaster.deleteAll(uniqueId, Flux.fromIterable(List.of(da1, da2)))).verifyComplete();
        checkCount(da1, 1);
        checkCount(da2, 1);
    }
}