package com.simple.datasourcing.error;

import lombok.extern.slf4j.*;

@Slf4j
public class TableNotExistException extends RuntimeException {

    public TableNotExistException(String message) {
        super(message);
        log.info("TableNotExistException :: {}", message);
    }
}