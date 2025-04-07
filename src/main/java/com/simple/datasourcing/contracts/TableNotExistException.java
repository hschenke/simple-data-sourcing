package com.simple.datasourcing.contracts;

import lombok.extern.slf4j.*;

@Slf4j
public class TableNotExistException extends RuntimeException {

    public TableNotExistException() {
        super();
        log.info("TableNotExistException :: one or more tables are not exist, please check");
    }
}