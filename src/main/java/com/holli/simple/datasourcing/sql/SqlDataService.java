package com.holli.simple.datasourcing.sql;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import com.holli.simple.datasourcing.contracts.connection.*;
import com.holli.simple.datasourcing.contracts.service.*;
import com.holli.simple.datasourcing.converter.*;
import com.holli.simple.datasourcing.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.jdbc.core.*;

import java.time.*;
import java.util.*;

@SuppressWarnings("SqlSourceToSinkFlow")
@Slf4j
public abstract class SqlDataService<T> extends DataService<T, JdbcTemplate, String> {

    private static final String WHERE_UNIQUE_ID_UNIQUE_ID = "where uniqueId = ?";
    private static final String ORDER_BY_TIMESTAMP_DESC_LIMIT_1 = WHERE_UNIQUE_ID_UNIQUE_ID + " order by timestamp desc limit 1";
    @Getter
    private final ObjectMapper objectMapper;

    public SqlDataService(DataConnection<JdbcTemplate> dataConnection, Class<T> clazz) {
        super(dataConnection, clazz);
        this.objectMapper = new ObjectMapper();
        configureObjectMapper();
    }

    private void configureObjectMapper() {
        // JavaTimeModule with custom serializer and deserializer for ZonedDateTime
        var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new ZonedDateTimeSerializer(CustomZonedDateTimeDeserializer.FORMATTER));
        javaTimeModule.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer());
        // Register the module
        objectMapper.registerModule(javaTimeModule);
        // Set other options
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    protected abstract String getFormattableCreateTableSql();

    @Override
    public void createBaseTable() {
        dataTemplate().execute(getFormattableCreateTableSql().formatted(getTableNameBase()));
    }

    @Override
    public void createHistoryTable() {
        dataTemplate().execute(getFormattableCreateTableSql().formatted(getTableNameHistory()));
    }

    @Override
    public boolean tableExists(String tableName) {
        try {
            return Objects.requireNonNull(dataTemplate().getDataSource())
                    .getConnection()
                    .getMetaData()
                    .getTables(null, null, tableName, new String[]{"TABLE"}).next();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String getQueryById(String uniqueId) {
        return WHERE_UNIQUE_ID_UNIQUE_ID;
    }

    @Override
    public String getQueryLastById(String uniqueId) {
        return ORDER_BY_TIMESTAMP_DESC_LIMIT_1;
    }

    @Override
    public boolean truncate(String tableName) {
        dataTemplate().execute("TRUNCATE TABLE %s".formatted(tableName));
        return true;
    }

    @Override
    public List<DataEvent<T>> findAll(String tableName) {
        return dataTemplate().query("select * from %s".formatted(tableName), getDataEventRowMapper());
    }

    @Override
    public List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName) {
        return dataTemplate().query("select * from %s %s".formatted(tableName, getQueryById(uniqueId)), getDataEventRowMapper(), uniqueId);
    }

    @Override
    public DataEvent<T> findLastBy(String uniqueId) {
        return dataTemplate().queryForObject("select * from %s %s".formatted(getTableNameBase(), getQueryLastById(uniqueId)), getDataEventRowMapper(), uniqueId);
    }

    @Override
    public Long countBy(String uniqueId, String tableName) {
        return dataTemplate().queryForObject("select count(*) from %s %s".formatted(tableName, getQueryById(uniqueId)), Long.class, uniqueId);
    }

    @Override
    public boolean removeBy(String uniqueId, String tableName) {
        return dataTemplate().update("DELETE FROM %s WHERE uniqueId = ?".formatted(tableName), uniqueId) > 0;
    }

    private RowMapper<DataEvent<T>> getDataEventRowMapper() {
        return (rs, _) ->
                DataEvent.<T>builder()
                        .uniqueId(rs.getString("uniqueId"))
                        .deleted(rs.getBoolean("deleted"))
                        .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                        .data(retrieveFromJson(rs.getString("data"), this.getClazz()))
                        .build();
    }

    public T retrieveFromJson(String jsonb, Class<T> type) {
        try {
            return objectMapper.readValue(jsonb, type);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}