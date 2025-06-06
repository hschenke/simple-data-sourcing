package com.simple.datasourcing.mongo;

import com.mongodb.*;
import com.mongodb.client.*;
import com.simple.datasourcing.contracts.connection.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Slf4j
public class MongoDataConnection extends DataConnection<MongoTemplate> {

    public MongoDataConnection(String dbUri) {
        super(dbUri);
    }

    @Override
    public MongoTemplate generateDataTemplate(String dbUri) {
        try {
            var connectionString = new ConnectionString(dbUri);
            var databaseFactory = new SimpleMongoClientDatabaseFactory(mongoClient(connectionString), Objects.requireNonNull(connectionString.getDatabase()));
            return new MongoTemplate(databaseFactory, MongoCommon.get().converters());
        } catch (Exception e) {
            log.error("Cannot create MongoTemplate for [{}] :: {}", dbUri, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public MongoClient mongoClient(ConnectionString connectionString) {
        return MongoClients.create(MongoCommon.get().clientSettings(connectionString));
    }
}