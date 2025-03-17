package com.simple.datasourcing;

import org.springframework.boot.test.context.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

@TestConfiguration(proxyBeanMethods = false)
public class MongoTestcontainersConfiguration {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    static {
        mongoDBContainer.start();
    }
}