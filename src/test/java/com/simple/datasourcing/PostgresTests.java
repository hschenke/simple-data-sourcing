package com.simple.datasourcing;

import com.simple.datasourcing.postgres.*;
import com.simple.datasourcing.support.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.test.annotation.*;

@Slf4j
@DirtiesContext
class PostgresTests extends DataSourcingTestBase {

    @BeforeAll
    static void setUp() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
    }

    public PostgresTests() {
        super(new PostgresDataMaster(postgreSQLContainer.getJdbcUrl()));
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