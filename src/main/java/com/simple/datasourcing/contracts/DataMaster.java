package com.simple.datasourcing.contracts;

import lombok.*;

@Getter
public abstract class DataMaster<DT> {

    private final DT dbTemplate;

    public DataMaster(String dbUri) {
        dbTemplate = generateDbTemplate(dbUri);
    }

    protected abstract DT generateDbTemplate(String dbUri);

    public abstract <T> Actions<T, ? extends DataService<T, ?, ?>> actionsFor(Class<T> clazz);

    @Getter
    public abstract class Actions<T, DA extends DataService<T, ?, ?>> {

        public record AllActions<T>(DataActionsBase<T> actionsBase, DataActionsHistory<T> actionsHistory) {
        }

        private final DA dataActions;

        public Actions(DT dbTemplate, Class<T> clazz) {
            dataActions = generateDataActions(dbTemplate, clazz);
        }

        protected abstract DA generateDataActions(DT dbTemplate, Class<T> clazz);

        public abstract DataActionsBase<T> getBase();

        public abstract DataActionsHistory<T> getHistory();

        public abstract AllActions<T> getAll();

    }
}