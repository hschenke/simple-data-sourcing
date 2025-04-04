package com.simple.datasourcing.contracts;

import java.util.*;

public interface DataMaster<DT> {

    DT getDataTemplate();

    <T> DataActions<T> getDataActions(Class<T> clazz);

    <T> DataActionsHistory<T> getDataActionsHistory(Class<T> clazz);

    <T> void initActionsFor(List<Class<T>> classes);
}