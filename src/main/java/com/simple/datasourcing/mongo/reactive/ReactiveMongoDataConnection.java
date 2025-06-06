package com.simple.datasourcing.mongo.reactive;

import com.mongodb.*;
import com.mongodb.reactivestreams.client.*;
import com.simple.datasourcing.contracts.connection.*;
import com.simple.datasourcing.mongo.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Slf4j
public class ReactiveMongoDataConnection extends DataConnection<ReactiveMongoTemplate> {

    public ReactiveMongoDataConnection(String dbUri) {
        super(dbUri);
    }

    @Override
    public ReactiveMongoTemplate generateDataTemplate(String dbUri) {
        try {
            var connectionString = new ConnectionString(dbUri);
            var databaseFactory = new SimpleReactiveMongoDatabaseFactory(mongoClient(connectionString), Objects.requireNonNull(connectionString.getDatabase()));
            return new ReactiveMongoTemplate(databaseFactory, MongoCommon.get().converters());
        } catch (Exception e) {
            log.error("Cannot create MongoTemplate for [{}] :: {}", dbUri, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public MongoClient mongoClient(ConnectionString connectionString) {
        return MongoClients.create(MongoCommon.get().clientSettings(connectionString));
    }
}