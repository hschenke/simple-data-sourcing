package com.holli.simple.datasourcing.contracts.connection;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
public abstract class DataConnection<DT> implements DataConnectionActions<DT> {

    private DT dataTemplate;

    public DataConnection(String dbUri) {
        this.dataTemplate = generateDataTemplate(dbUri);
    }

    protected abstract DT generateDataTemplate(String dbUri);
}