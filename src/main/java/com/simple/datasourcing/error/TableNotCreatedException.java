package com.simple.datasourcing.error;

import lombok.extern.slf4j.*;

@Slf4j
public class TableNotCreatedException extends RuntimeException {

    public TableNotCreatedException(String message) {
        super(message);
        log.error("TableNotCreatedException :: {}", message);
    }
}