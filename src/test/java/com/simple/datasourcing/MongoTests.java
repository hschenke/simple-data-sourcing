package com.simple.datasourcing;

import com.simple.datasourcing.mongo.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

@Slf4j
class MongoTests extends SimpleDataSourcingTestBase {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    static MongoDataMaster mongoDataMaster;

    static {
        mongoDBContainer.start();
        mongoDataMaster = new MongoDataMaster(mongoDBContainer.getReplicaSetUrl());
    }

//    MongoDataActions<TestData1> da1;
//    MongoDataActions<TestData1>.History da1History;

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