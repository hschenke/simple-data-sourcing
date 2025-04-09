package com.simple.datasourcing.contracts;

import lombok.extern.slf4j.*;

@Slf4j
public class TablesNotExistException extends RuntimeException {

    public TablesNotExistException() {
        super();
        log.info("TableNotExistException :: one or more tables are not existing, please check");
    }
}