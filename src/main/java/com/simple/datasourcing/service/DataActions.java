package com.simple.datasourcing.service;

import com.simple.datasourcing.model.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class DataActions<T> {

    private final DataEventRepository<T> repo;

    public DataActions(DataEventRepository<T> repo) {
        this.repo = repo;
    }

    public List<DataEvent<T>> getAllFor(String uniqueId) {
        return repo.findAllByUniqueId(uniqueId);
    }

    public DataEvent<T> getLastFor(String uniqueId) {
        return repo.findFirstByUniqueIdOrderByTimestampDesc(uniqueId);
    }

    public DataEvent<T> createFor(String uniqueId, T data) {
        return repo.save(new DataEvent<>(uniqueId, data));
    }

    public DataEvent<T> deleteFor(String uniqueId) {
        return repo.save(new DataEvent<>(uniqueId, true));
    }
}