package com.simple.datasourcing.mongo;

import com.mongodb.*;
import com.simple.datasourcing.converter.*;
import org.bson.codecs.configuration.*;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.*;

public class MongoCommon {

    private MongoCommon() {
    }

    public static MongoCommon get() {
        return new MongoCommon();
    }

    public MappingMongoConverter converters() {
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

    public MongoClientSettings clientSettings(ConnectionString connectionString) {
        // Combine with default codecs
        var codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromCodecs(new ZonedDateTimeCodec()),
                fromProviders(new ZonedDateTimeCodec.ZonedDateTimeCodecProvider())
        );
        // Configure MongoDB client with codec registry
        return MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
    }
}