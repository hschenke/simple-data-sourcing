package com.simple.datasourcing.postgres;

import com.simple.datasourcing.contracts.connection.*;
import lombok.extern.slf4j.*;
import org.springframework.boot.jdbc.*;
import org.springframework.jdbc.core.*;

import javax.sql.*;
import java.net.*;

@Slf4j
public class PostgresDataConnection extends DataConnection<JdbcTemplate> {

    public PostgresDataConnection(String dbUri) {
        super(dbUri);
    }

    @Override
    public JdbcTemplate generateDataTemplate(String dbUri) {
        return new JdbcTemplate(dataSource(dbUri));
    }

    public DataSource dataSource(String dbUri) {
        try {
            if (dbUri.startsWith("jdbc:")) dbUri = dbUri.substring(5);
            var uri = new URI(dbUri);
            var username = uri.getUserInfo() == null ? "test" : uri.getUserInfo().split(":")[0];
            var password = uri.getUserInfo() == null ? "test" : uri.getUserInfo().split(":")[1];
            var url = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();
            return DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Invalid DB URI", e);
        }
    }
}