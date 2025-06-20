package com.holli.simple.datasourcing.mongo;

import com.holli.simple.datasourcing.converter.*;
import com.mongodb.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.*;
import org.springframework.core.convert.converter.*;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;

import static org.bson.codecs.configuration.CodecRegistries.*;

public class MongoCommon {

    private List<Converter<?, ?>> converters = new ArrayList<>();
    private List<Codec<?>> codecs = new ArrayList<>();

    private MongoCommon() {
        addConverter(new ZonedDateTimeConverter.ZonedDateTimeToStringConverter());
        addConverter(new ZonedDateTimeConverter.StringToZonedDateTimeConverter());
        addCodec(new ZonedDateTimeCodec());
    }

    public static MongoCommon get() {
        return new MongoCommon();
    }

    public MappingMongoConverter converters() {
        var customConversions = new MongoCustomConversions(converters);

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
                fromCodecs(codecs),
                fromProviders(new ZonedDateTimeCodec.ZonedDateTimeCodecProvider())
        );
        // Configure MongoDB client with codec registry
        return MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
    }

    public void addConverter(Converter<?, ?> converter) {
        this.converters.add(converter);
    }

    public void addConverters(List<Converter<?, ?>> converters) {
        this.converters.addAll(converters == null ? List.of() : converters);
    }

    public void addCodec(Codec<?> codec) {
        this.codecs.add(codec);
    }

    public void addCodecs(List<Codec<?>> codecs) {
        this.codecs.addAll(codecs == null ? List.of() : codecs);
    }
}