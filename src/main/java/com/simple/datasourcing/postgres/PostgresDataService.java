package com.simple.datasourcing.postgres;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.simple.datasourcing.contracts.*;
import com.simple.datasourcing.model.*;
import lombok.extern.slf4j.*;
import org.postgresql.util.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.core.simple.*;

import java.sql.*;
import java.util.*;

@SuppressWarnings("SqlSourceToSinkFlow")
@Slf4j
public class PostgresDataService<T> extends DataService<T, JdbcTemplate, String> {

    private static final String WHERE_UNIQUE_ID_UNIQUE_ID = "where uniqueId = ?";
    private static final String ORDER_BY_TIMESTAMP_DESC_LIMIT_1 = WHERE_UNIQUE_ID_UNIQUE_ID + " order by timestamp desc limit 1";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostgresDataService(PostgresDataConnection postgresDataConnection, Class<T> clazz) {
        super(postgresDataConnection, clazz);
    }

    @Override
    public void createTables() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS %s (
                        uniqueId TEXT,
                        deleted BOOLEAN,
                        timestamp TIMESTAMP,
                        data JSONB
                    );
                """;
        dataTemplate().execute(sql.formatted(getTableNameBase()));
        dataTemplate().execute(sql.formatted(getTableNameHistory()));
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
        log.info("Truncating table {}", tableName);
        dataTemplate().execute("TRUNCATE TABLE %s".formatted(tableName));
        return true;
    }

    @Override
    public List<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName) {
        return dataTemplate().query("select * from %s %s".formatted(tableName, getQueryById(uniqueId)), getDataEventRowMapper(), uniqueId);
    }

    @Override
    public DataEvent<T> findLastBy(String uniqueId) {
        return dataTemplate().queryForObject("select * from %s %s".formatted(getTableNameBase(), getQueryLastById(uniqueId)), getDataEventRowMapper(), uniqueId);
    }

    private RowMapper<DataEvent<T>> getDataEventRowMapper() {
        return (rs, rowNum) ->
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

    @Override
    public Long countBy(String uniqueId, String tableName) {
        return dataTemplate().queryForObject("select count(*) from %s %s".formatted(tableName, getQueryById(uniqueId)), Long.class, uniqueId);
    }

    @Override
    public boolean removeBy(String uniqueId, String tableName) {
        String sql = "DELETE FROM %s WHERE uniqueId = ?".formatted(tableName);
        return dataTemplate().update(sql, uniqueId) > 0;
    }

    @Override
    public boolean insertBy(DataEvent<T> dataEvent) {
        var sql = "INSERT INTO %s (uniqueId, deleted, timestamp, data) VALUES (?, ?, ?, ?)".formatted(getTableNameBase());
        return dataTemplate().update(connection -> {
            var ps = connection.prepareStatement(sql);
            ps.setObject(1, dataEvent.getUniqueId());//, Types.VARCHAR);
            ps.setObject(2, dataEvent.getDeleted());//, Types.BOOLEAN);
            ps.setObject(3, dataEvent.getTimestamp(), Types.TIMESTAMP);
            ps.setObject(4, transformToJsonb(dataEvent.getData()), Types.OTHER);
            return ps;
        }) > 0;
    }

    public PGobject transformToJsonb(T data) throws SQLException {
        var jsonb = new PGobject();
        jsonb.setType("jsonb");
        try {
            jsonb.setValue(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return jsonb;
    }

    @Override
    public boolean dataHistorization(String uniqueId) {
        try {
            var insert = new SimpleJdbcInsert(dataTemplate()).withTableName(getTableNameHistory());
            var batch = findAllEventsBy(uniqueId, getTableNameBase()).stream()
                    .map(dataEvent -> {
                        Map<String, Object> params = new HashMap<>();
                        params.put("uniqueId", dataEvent.getUniqueId());
                        params.put("deleted", dataEvent.getDeleted());
                        params.put("timestamp", dataEvent.getTimestamp());
                        try {
                            params.put("data", transformToJsonb(dataEvent.getData()));
                        } catch (SQLException e) {
                            log.error(e.getMessage());
                        }
                        return params;
                    })
                    .map(MapSqlParameterSource::new)
                    .toArray(SqlParameterSource[]::new);

            insert.executeBatch(batch);
            removeBy(uniqueId, getTableNameBase());

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}