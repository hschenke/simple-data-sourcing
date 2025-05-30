package com.simple.datasourcing.mongo;

import com.mongodb.*;
import com.mongodb.client.*;
import com.simple.datasourcing.contracts.*;
import com.simple.datasourcing.mongo.converter.*;
import lombok.extern.slf4j.*;
import org.bson.codecs.configuration.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.*;

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
            return new MongoTemplate(databaseFactory, converters());
        } catch (Exception e) {
            log.error("Cannot create MongoTemplate for [{}] :: {}", dbUri, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private MappingMongoConverter converters() {
        var customConversions = new MongoCustomConversions(List.of(
                new ZonedDateTimeConverter.ZonedDateTimeToStringConverter(),
                new ZonedDateTimeConverter.StringToZonedDateTimeConverter()
        ));

        var mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();

        var converter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
        converter.setCustomConversions(customConversions);
        converter.afterPropertiesSet();

        return converter;
    }

    public MongoClient mongoClient(ConnectionString connectionString) {
        // Combine with default codecs
        var codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromCodecs(new ZonedDateTimeCodec()),
                fromProviders(new ZonedDateTimeCodec.ZonedDateTimeCodecProvider())
        );
        // Configure MongoDB client with codec registry
        var settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
        return MongoClients.create(settings);
    }
}