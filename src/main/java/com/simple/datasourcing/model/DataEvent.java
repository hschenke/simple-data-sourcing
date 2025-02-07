package com.simple.datasourcing.model;


import lombok.*;

import java.time.*;


@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
//@Table(name = DataEvent.tableName )
public class DataEvent<T> {

  //  public static final String tableName =;

    private String uniqueId;
    private String description;
    private boolean deleted;
    private LocalDateTime timestamp;
    private T data;

    public DataEvent() {
        this(null);
    }

    public DataEvent(String uniqueId, boolean deleted) {
        this(uniqueId);
        this.deleted = deleted;
    }

    public DataEvent(String uniqueId, T data) {
        this(uniqueId);
        this.data = data;
    }

    public DataEvent(String uniqueId) {
        this.uniqueId = uniqueId;
        timestamp = LocalDateTime.now();
    }
}