package com.simple.datasourcing.contracts;

public interface DataConnectionActions<DT> {

    DT generateDataTemplate(String dbUri);

    DT getDataTemplate();
}