package com.simple.datasourcing.postgres;

import com.fasterxml.jackson.core.*;
import com.simple.datasourcing.model.*;
import com.simple.datasourcing.sql.*;
import lombok.extern.slf4j.*;
import org.postgresql.util.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.core.simple.*;

import java.sql.*;
import java.util.*;

@Slf4j
public class PostgresDataService<T> extends SqlDataService<T> {

    public PostgresDataService(PostgresDataConnection postgresDataConnection, Class<T> clazz) {
        super(postgresDataConnection, clazz);
    }

    @Override
    protected String getFormattableCreateTableSql() {
        return """
                    CREATE TABLE IF NOT EXISTS %s (
                        uniqueId TEXT,
                        deleted BOOLEAN,
                        timestamp TIMESTAMP,
                        data JSONB
                    );
                """;
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

    @Override
    public boolean moveToHistory(String uniqueId) {
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

        var ints = new SimpleJdbcInsert(dataTemplate()).withTableName(getTableNameHistory()).executeBatch(batch);
        return ints.length > 0;
    }

    @Override
    public boolean removeFromBase(String uniqueId) {
        return removeBy(uniqueId, getTableNameBase());
    }

    public PGobject transformToJsonb(T data) throws SQLException {
        var jsonb = new PGobject();
        jsonb.setType("jsonb");
        try {
            jsonb.setValue(getObjectMapper().writeValueAsString(data));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return jsonb;
    }
}