package com.simple.datasourcing.service;

import lombok.*;
import org.springframework.data.mongodb.core.*;

@Getter
public class DataCon {

    private final SimpleMongoClientDatabaseFactory factory;
    private final MongoTemplate template;

    public DataCon(String mongoUri) {
        this.factory = new SimpleMongoClientDatabaseFactory(mongoUri);
        this.template = new MongoTemplate(factory);
    }
}