package com.simple.datasourcing.support;

import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

public abstract class TestcontainerSetup extends TestDataBase {

    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            //.withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(27017), new ExposedPort(27017)))))
            .withReuse(true);

    @SuppressWarnings("resource")
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            //.withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)))))
            .withReuse(true);

    @BeforeEach
    void setup() {
        truncateData();
    }

    protected abstract void truncateData();
}