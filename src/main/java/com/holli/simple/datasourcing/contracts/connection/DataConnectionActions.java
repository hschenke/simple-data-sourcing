package com.holli.simple.datasourcing.contracts.connection;

public interface DataConnectionActions<DT> {

    DT getDataTemplate();

    void setDataTemplate(DT dataTemplate);
}