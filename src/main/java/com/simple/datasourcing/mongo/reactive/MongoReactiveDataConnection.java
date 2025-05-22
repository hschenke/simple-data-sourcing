package com.simple.datasourcing.mongo.reactive;

import com.mongodb.*;
import com.simple.datasourcing.contracts.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

@Slf4j
public class MongoReactiveDataConnection extends DataConnection<ReactiveMongoTemplate> {

    public MongoReactiveDataConnection(String dbUri) {
        super(dbUri);
    }

    @Override
    public ReactiveMongoTemplate generateDataTemplate(String dbUri) {
        return new ReactiveMongoTemplate(new SimpleReactiveMongoDatabaseFactory(new ConnectionString(dbUri)));
    }
}