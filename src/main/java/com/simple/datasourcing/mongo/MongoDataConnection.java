package com.simple.datasourcing.mongo;

import com.simple.datasourcing.contracts.*;
import lombok.extern.slf4j.*;
import org.springframework.data.mongodb.core.*;

@Slf4j
public class MongoDataConnection extends DataConnection<MongoTemplate> {

    public MongoDataConnection(String dbUri) {
        super(dbUri);
    }

    @Override
    public MongoTemplate generateDataTemplate(String dbUri) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(dbUri));
    }
}