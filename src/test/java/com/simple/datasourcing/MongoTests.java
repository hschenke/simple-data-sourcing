package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

@Slf4j
class MongoTests extends DataSourcingTestBase {

    static MongoDataMaster mongoDataMaster = new MongoDataMaster(mongoDBContainer.getReplicaSetUrl());

    public MongoTests() {
        super(mongoDataMaster.getDataActions(TestData1.class), mongoDataMaster.getDataActions(TestData2.class));
    }

    @Test
    void testMe() {
        dataMasterTest();
        dataAllActionsTest();
        var service = mongoDataMaster.getDataActions(TestData1.class).getService();
        tableExistsTest(service, tableName -> service.dataTemplate().dropCollection(tableName));
    }
}