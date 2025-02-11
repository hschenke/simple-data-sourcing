package com.simple.datasourcing.model;


import lombok.*;

import java.time.*;


@Getter
@Setter
@ToString
public class DataEvent<T> {

    private String uniqueId;
    private boolean deleted;
    private LocalDateTime timestamp;
    private T data;

    public DataEvent() {
        timestamp = LocalDateTime.now();
    }

    public DataEvent<T> setData(String uniqueId, boolean deleted, T data) {
        this.uniqueId = uniqueId;
        this.deleted = deleted;
        this.data = data;
        return this;
    }
}