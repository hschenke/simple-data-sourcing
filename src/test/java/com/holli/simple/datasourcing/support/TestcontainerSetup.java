package com.holli.simple.datasourcing.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class TestcontainerSetup {

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