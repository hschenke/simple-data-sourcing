package com.simple.datasourcing.contracts;

import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
public abstract class DataConnection<DT> implements DataConnectionActions<DT> {

    private final DT dataTemplate;

    public DataConnection(String dbUri) {
        this.dataTemplate = generateDataTemplate(dbUri);
    }

    protected abstract DT generateDataTemplate(String dbUri);
}