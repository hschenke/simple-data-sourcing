package com.simple.datasourcing.model;

import org.springframework.data.mongodb.repository.*;

import java.util.*;

public interface DataEventRepository<T> extends MongoRepository<DataEvent<T>, String> {
    List<DataEvent<T>> findAllByUniqueId(String uniqueId);
    DataEvent<T> findFirstByUniqueIdOrderByTimestampDesc(String uniqueId);
}