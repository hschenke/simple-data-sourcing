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

    private DataEvent() {
    }

    public static <T> DataEvent<T> create() {
        return new DataEvent<>();
    }

    public DataEvent<T> setData(String uniqueId, boolean deleted, T data) {
        this.uniqueId = uniqueId;
        this.deleted = deleted;
        this.data = data;
        this.timestamp = LocalDateTime.now();
        return this;
    }
}