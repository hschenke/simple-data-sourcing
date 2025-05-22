package com.simple.datasourcing;

import com.github.dockerjava.api.model.*;
import com.simple.datasourcing.postgres.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

@Slf4j
class PostgresTests extends DataSourcingTestBase {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(
                            Ports.Binding.bindPort(5432), new ExposedPort(5432)))))
            .withReuse(true);
    static PostgresDataMaster postgresDataMaster;

    static {
        postgreSQLContainer.start();
        postgresDataMaster = new PostgresDataMaster(postgreSQLContainer.getJdbcUrl());
    }

//    PostgresDataActions<TestData1> da1;
//    PostgresDataActions<TestData1>.History da1History;

    public PostgresTests() {
        super(postgresDataMaster.getDataActions(TestData1.class), postgresDataMaster.getDataActions(TestData2.class));
    }

    @Test
    void dataMasterTestx() {
        dataMasterTest();
    }

    @Test
    void dataAllActionsTestx() {
        dataAllActionsTest();
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    @Test
    void tableExistsTest() {
        var service = postgresDataMaster.getDataActions(TestData1.class).getService();
        tableExistsTest(service, tableName -> service.dataTemplate().execute("DROP TABLE " + tableName));
    }
}