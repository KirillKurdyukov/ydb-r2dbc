/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ydb.io.r2dbc;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import reactor.core.publisher.Mono;
import tech.ydb.io.r2dbc.state.YDBConnectionState;
import tech.ydb.table.TableClient;

import java.time.Duration;

/**
 * @author Kirill Kurdyukov
 */
public class YDBConnection implements Connection {

    private final TableClient tableClient;

    private volatile IsolationLevel isolationLevel = IsolationLevel.SERIALIZABLE;
    private volatile boolean autoCommit = true;
    private volatile YDBConnectionState state;

    public YDBConnection(TableClient tableClient) {
        this.tableClient = tableClient;
    }

    @Override
    public Mono<Void> beginTransaction() {
        return null;
    }

    @Override
    public Mono<Void> beginTransaction(TransactionDefinition definition) {
        return null;
    }

    @Override
    public Mono<Void> close() {
        return Mono.empty();
    }

    @Override
    public Mono<Void> commitTransaction() {

        return null;
    }

    @Override
    public Batch createBatch() {
        return new YDBBatch(state);
    }

    @Override
    public Mono<Void> createSavepoint(String name) {
        throw new UnsupportedOperationException("YDB R2DBC driver is unsupported savepoint");
    }

    @Override
    public Statement createStatement(String sql) {
        return new YDBStatement(state, sql);
    }

    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }

    @Override
    public ConnectionMetadata getMetadata() {
        return null;
    }

    @Override
    public IsolationLevel getTransactionIsolationLevel() {
        return isolationLevel;
    }

    @Override
    public Mono<Void> releaseSavepoint(String name) {
        throw new UnsupportedOperationException("YDB R2DBC driver is unsupported savepoint");
    }

    @Override
    public Mono<Void> rollbackTransaction() {
        return null;
    }

    @Override
    public Mono<Void> rollbackTransactionToSavepoint(String name) {
        throw new UnsupportedOperationException("YDB R2DBC driver is unsupported savepoint");
    }

    @Override
    public Mono<Void> setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;

        return Mono.empty();
    }

    @Override
    public Mono<Void> setLockWaitTimeout(Duration timeout) {
        return null;
    }

    @Override
    public Mono<Void> setStatementTimeout(Duration timeout) {
        return null;
    }

    @Override
    public Mono<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
        this.isolationLevel = isolationLevel;
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> validate(ValidationDepth depth) {
        return Mono.just(true);
    }
}
