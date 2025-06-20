package com.holli.simple.datasourcing.contracts.service;

import com.holli.simple.datasourcing.contracts.connection.*;
import com.holli.simple.datasourcing.error.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public abstract class DataService<T, DT, Q> implements DataServiceActions<T, Q> {

    private final DataConnection<DT> dataConnection;
    private final Class<T> clazz;
    private final String tableNameBase;
    private final String tableNameHistory;

    protected DataService(DataConnection<DT> dataConnection, Class<T> clazz) {
        this.dataConnection = dataConnection;
        this.clazz = clazz;
        this.tableNameBase = clazz.getSimpleName().toLowerCase();
        this.tableNameHistory = tableNameBase + "_history";
        if (!tableExists(tableNameBase) || !tableExists(tableNameHistory)) {
            log.info("Creating tables :: {} and {}", tableNameBase, tableNameHistory);
            createBaseTable();
            if (!tableExists(tableNameBase)) throw new TableNotCreatedException(this.tableNameBase);
            createHistoryTable();
            if (!tableExists(tableNameHistory)) throw new TableNotCreatedException(this.tableNameHistory);
        }
    }

    public DT dataTemplate() {
        return dataConnection.getDataTemplate();
    }
}