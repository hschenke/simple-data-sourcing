package com.simple.datasourcing.mongo.reactive;

import com.mongodb.bulk.*;
import com.mongodb.client.result.*;
import com.simple.datasourcing.contracts.reactive.*;
import com.simple.datasourcing.model.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import reactor.core.publisher.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;

@Slf4j
public class ReactiveMongoDataService<T> extends ReactiveDataService<T, ReactiveMongoTemplate, Query> {

    public ReactiveMongoDataService(ReactiveMongoDataConnection mongoDataConnection, Class<T> clazz) {
        super(mongoDataConnection, clazz);
    }

    @Override
    public Mono<String> createBaseTable() {
        return createCollection(getTableNameBase());
    }

    @Override
    public Mono<String> createHistoryTable() {
        return createCollection(getTableNameHistory());
    }

    private Mono<String> createCollection(String tableName) {
        log.info("Attempting to create collection :: {}", tableName);
        return uncheckedDataTemplate().createCollection(tableName) // only for test purpose -> .then(Mono.error(new TableNotCreatedException(tableName)));
                .map(createdCollection -> createdCollection.getNamespace().getCollectionName())
                .doOnNext(collectionName -> log.info("Collection [{}] created successfully.", collectionName))
                .doOnError(error -> log.error("Failed to create collection :: {}", error.getMessage()));
    }

    @Override
    public Mono<Boolean> tableExists(String tableName) {
        return uncheckedDataTemplate()
                .collectionExists(tableName)
                .doOnNext(exists -> log.info("Collection [{}] exists :: {}", tableName, exists));
    }

    @Override
    public Query getQueryById(String uniqueId) {
        return new Query()
                .addCriteria(where("uniqueId").is(uniqueId));
    }

    @Override
    public Query getQueryLastById(String uniqueId) {
        return getQueryById(uniqueId)
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(1);
    }

    @Override
    public Mono<Boolean> truncate(String tableName) {
        log.info("Truncating table {}", tableName);
        return checkedDataTemplate().flatMap(dt -> dt
                        .remove(new Query(), tableName))
                .map(DeleteResult::wasAcknowledged);
    }

    //TODO: get rid of warning
    @SuppressWarnings("unchecked")
    @Override
    public Flux<DataEvent<T>> findAllEventsBy(String uniqueId, String tableName) {
        return (Flux<DataEvent<T>>) checkedDataTemplate()
                .flatMapMany(dt -> dt
                        .find(getQueryById(uniqueId), DataEvent.create().getClass(), tableName));
    }

    @Override
    public Mono<Long> countBy(String uniqueId, String tableName) {
        return checkedDataTemplate()
                .flatMap(dt -> dt.count(getQueryById(uniqueId), tableName))
                .doOnSuccess(count -> log.info("Count of [{}] in table [{}] :: {}", uniqueId, tableName, count));
    }

    @Override
    public Mono<DeleteResult> removeBy(String uniqueId, String tableName) {
        return checkedDataTemplate().flatMap(dt -> dt.remove(getQueryById(uniqueId), tableName));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Mono<DataEvent<T>> findLastBy(String uniqueId) {
        return (Mono<DataEvent<T>>) checkedDataTemplate().flatMap(dt ->
                dt.findOne(getQueryLastById(uniqueId), DataEvent.create().getClass(), getTableNameBase()))
                .doOnSuccess(lastData -> log.info("Last data found :: {}", lastData));
    }

    @Override
    public Mono<DataEvent<T>> insertBy(DataEvent<T> dataEvent) {
        return checkedDataTemplate().flatMap(dt -> dt.insert(dataEvent, getTableNameBase()));
    }

    @Override
    public Mono<Boolean> moveToHistory(String uniqueId) {
        return checkedDataTemplate()
                .flatMap(dt -> findAllEventsBy(uniqueId, getTableNameBase())
                        .collectList()
                        .flatMap(list -> dt.
                                bulkOps(BulkOperations.BulkMode.ORDERED, getTableNameHistory())
                                .insert(list)
                                .execute()
                        )
                )
                .map(BulkWriteResult::wasAcknowledged);
    }

    @Override
    public Mono<Boolean> removeFromBase(String uniqueId) {
        return uncheckedDataTemplate()
                .bulkOps(BulkOperations.BulkMode.UNORDERED, getTableNameBase())
                .remove(getQueryById(uniqueId))
                .execute()
                .map(BulkWriteResult::wasAcknowledged);
    }
}