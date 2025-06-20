package com.holli.simple.datasourcing.mongo;

import com.holli.simple.datasourcing.contracts.connection.*;
import com.mongodb.*;
import com.mongodb.client.*;
import lombok.extern.slf4j.*;
import org.bson.codecs.*;
import org.springframework.core.convert.converter.*;
import org.springframework.data.mongodb.core.*;

import java.util.*;

@Slf4j
public class MongoDataConnection extends DataConnection<MongoTemplate> {

    private final List<Converter<?, ?>> converters;
    private final List<Codec<?>> codecs;

    public MongoDataConnection(String dbUri, List<Converter<?, ?>> converters, List<Codec<?>> codecs) {
        super(dbUri);
        this.converters = converters;
        this.codecs = codecs;
        // if additional converters or codecs, do generateDataTemplate again to inject them
        if (converters != null || codecs != null) super.setDataTemplate(generateDataTemplate(dbUri));
    }

    @Override
    public MongoTemplate generateDataTemplate(String dbUri) {
        var mongoCommon = MongoCommon.get();
        mongoCommon.addConverters(this.converters);
        mongoCommon.addCodecs(this.codecs);
        try {
            var connectionString = new ConnectionString(dbUri);
            var databaseFactory = new SimpleMongoClientDatabaseFactory(
                    MongoClients.create(mongoCommon.clientSettings(connectionString)),
                    Objects.requireNonNull(connectionString.getDatabase())
            );
            return new MongoTemplate(databaseFactory, mongoCommon.converters());
        } catch (Exception e) {
            log.error("Cannot create MongoTemplate for [{}] :: {}", dbUri, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}