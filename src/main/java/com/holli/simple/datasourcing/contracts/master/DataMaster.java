package com.holli.simple.datasourcing.contracts.master;

import com.holli.simple.datasourcing.contracts.actions.*;
import com.holli.simple.datasourcing.contracts.connection.*;
import com.holli.simple.datasourcing.contracts.service.*;
import com.holli.simple.datasourcing.thread.*;
import lombok.*;
import lombok.extern.slf4j.*;

import java.util.*;

@Slf4j
@Getter
public abstract class DataMaster implements DataMasterActions {

    private final String dbUri;
    private final DataConnection<?> dataConn;

    protected DataMaster(String dbUri) {
        this.dbUri = dbUri;
        this.dataConn = generateDataConnection();
    }

    protected abstract DataConnection<?> generateDataConnection();

    protected abstract <T> DataService<T, ?, ?> getDataService(Class<T> clazz);

    @Override
    public void deleteAll(String uniqueId, DataActions<?>... dataActions) {
        Arrays.stream(dataActions).forEach(dataAction ->
                dataAction.delete(uniqueId)
        );
    }

    @Override
    public ThreadMaster deleteAllInBackground(String uniqueId, DataActions<?>... dataActions) {
        return ThreadMaster.get().run(() -> deleteAll(uniqueId, dataActions), e -> log.error("Error while deleting data", e));
    }
}