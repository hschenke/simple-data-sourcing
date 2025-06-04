package com.simple.datasourcing.threaded;

import com.simple.datasourcing.contracts.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
public abstract class ThreadDataMaster extends DataMaster {

    protected ThreadDataMaster(String dbUri) {
        super(dbUri);
    }
}