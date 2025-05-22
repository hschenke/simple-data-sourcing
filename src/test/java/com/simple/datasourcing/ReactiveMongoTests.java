package com.simple.datasourcing;

import com.simple.datasourcing.mongo.reactive.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

@Slf4j
class ReactiveMongoTests extends ReactiveDataSourcingTestBase {

    static MongoReactiveDataMaster dataMaster = new MongoReactiveDataMaster(mongoDBContainer.getReplicaSetUrl());

    public ReactiveMongoTests() {
        super(dataMaster.getDataActions(TestData1.class), dataMaster.getDataActions(TestData2.class));
    }

    @Test
    void testMe() {
        tableExistsTest();
        dataAllActionsTest();
    }
}