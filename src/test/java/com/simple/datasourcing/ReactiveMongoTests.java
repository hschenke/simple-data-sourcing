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
        super(new MongoReactiveDataMaster(mongoDBContainer.getReplicaSetUrl()));
    }

    @Test
    void testMe() {
        tableExistsTest();
        dataAllActionsTest();
    }

//    @Test
//    void testReactiveMongo() {
//        var dataActions = dataMaster.getDataActions(TestData2.class);
//        var mongoService = dataActions.getMongoService();
//
//        var allEventsBy = mongoService.findAllEventsBy(uniqueId, mongoService.getTableNameBase());
//
//        StepVerifier.create(allEventsBy).verifyComplete();
//        StepVerifier.create(dataActions.countFor(uniqueId)).expectNext(0l).verifyComplete();
//
//        StepVerifier.create(dataActions.createFor(uniqueId, testData2)).expectNext(true).verifyComplete();
//
//        StepVerifier.create(dataActions.countFor(uniqueId)).expectNext(1l).verifyComplete();
//        StepVerifier.create(allEventsBy.log()).expectNextMatches(Objects::nonNull).verifyComplete();
//
//        StepVerifier.create(dataActions.history().countFor(uniqueId)).expectNext(0L).verifyComplete();
//        StepVerifier.create(mongoService.moveToHistory(uniqueId))
//                .expectNext(true)
//                .verifyComplete();
//        StepVerifier.create(dataActions.history().countFor(uniqueId)).expectNext(1L).verifyComplete();
//    }
}