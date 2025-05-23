package com.simple.datasourcing;

import com.simple.datasourcing.contracts.reactive.*;
import lombok.extern.slf4j.*;
import reactor.test.*;

@Slf4j
class ReactiveDataSourcingTestBase extends TestData {

    ReactiveDataActions<TestData1> da1;
    ReactiveDataActions<TestData1>.History da1History;
    ReactiveDataService<TestData1, ?, ?> service1;
    ReactiveDataActions<TestData2> da2;

    public ReactiveDataSourcingTestBase(ReactiveDataActions<TestData1> da1, ReactiveDataActions<TestData2> da2) {
        this.da1 = da1;
        this.da1History = da1.history();
        this.service1 = da1.getService();
        this.da2 = da2;
    }

    void truncateData() {
        da1.truncate().subscribe();
        da1History.truncate().subscribe();
    }

    void dataAllActionsTest() {
        StepVerifier.create(da2.createFor(uniqueId, testData2))
                .expectNext(true).verifyComplete();
        StepVerifier.create(da2.countFor(uniqueId))
                .expectNext(1L).verifyComplete();

        StepVerifier.create(da2.history().countFor(uniqueId))
                .expectNext(0L).verifyComplete();
        StepVerifier.create(da2.history().dataHistorization(uniqueId))
                .expectNext(true).verifyComplete();
        StepVerifier.create(da2.history().countFor(uniqueId))
                .expectNext(1L).verifyComplete();

        StepVerifier.create(da2.countFor(uniqueId))
                .expectNext(0L).verifyComplete();

        truncateData();
    }

    void tableExistsTest() {
        StepVerifier.create(service1.tableExists(service1.getTableNameBase()))
                .expectNext(false)
                .verifyComplete();

        StepVerifier.create(service1.createBaseTable())
                .expectNext(service1.getTableNameBase())
                .verifyComplete();

        StepVerifier.create(service1.tableExists(service1.getTableNameBase()))
                .expectNext(true)
                .verifyComplete();
    }
}