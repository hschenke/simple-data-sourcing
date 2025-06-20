package com.holli.simple.datasourcing.mongo;

import com.holli.simple.datasourcing.contracts.master.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.bson.codecs.*;
import org.springframework.core.convert.converter.*;

import java.util.*;

@Slf4j
@Getter
public class MongoDataMaster extends DataMaster {

    private List<Converter<?, ?>> converters;
    private List<Codec<?>> codecs;

    public MongoDataMaster(String dbUri) {
        super(dbUri);
    }

    public MongoDataMaster(String dbUri, List<Converter<?, ?>> converters, List<Codec<?>> codecs) {
        super(dbUri);
        this.converters = converters;
        this.codecs = codecs;
    }

    @Override
    protected MongoDataConnection generateDataConnection() {
        return new MongoDataConnection(getDbUri(), converters, codecs);
    }

    @Override
    protected <T> MongoDataService<T> getDataService(Class<T> clazz) {
        return new MongoDataService<>(generateDataConnection(), clazz);
    }

    @Override
    public <T> MongoDataActions<T> getDataActions(Class<T> clazz) {
        return new MongoDataActions<>(getDataService(clazz));
    }
}