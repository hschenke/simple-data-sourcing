package com.simple.datasourcing.support;

import com.github.dockerjava.api.model.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

import java.time.*;
import java.util.*;

public abstract class TestDataAndSetup {

    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(
                            Ports.Binding.bindPort(27017), new ExposedPort(27017)))))
            .withReuse(true);

    @SuppressWarnings("resource")
    @ServiceConnection
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(
                            Ports.Binding.bindPort(5432), new ExposedPort(5432)))))
            .withReuse(true);

    public static String uniqueId = "holli";
    public static String uniqueIdNext = "holli-next";

    public TestData1 testData1 = new TestData1("id-1-1", "name-1-1", 1.1);
    public TestData1 testData1_2 = new TestData1("id-1-2", "name-1-2", 1.2);
    public TestData1 testData1_next = new TestData1("id-1-1-next", "name-1-1", 1.19);
    public TestData2 testData2 = new TestData2("id-2-1", List.of(testData1, testData1_2));
    public TestData3 testData3 = new TestData3("id-3-1", ZonedDateTime.now());

    @BeforeEach
    void setup() {
        truncateData();
    }

    protected abstract void truncateData();

    public sealed interface TestData permits TestData1, TestData2, TestData3 {
    }

    public record TestData1(String id, String name, Object data) implements TestData {
    }

    public record TestData2(String id, List<TestData1> testData1s) implements TestData {
    }

    public record TestData3(String id, ZonedDateTime dateTime) implements TestData {
    }
}