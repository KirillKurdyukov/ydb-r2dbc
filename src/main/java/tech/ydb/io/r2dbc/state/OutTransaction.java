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

package tech.ydb.io.r2dbc.state;

import tech.ydb.core.Result;
import tech.ydb.io.r2dbc.util.ResultExtractor;
import tech.ydb.table.Session;
import tech.ydb.table.TableClient;
import tech.ydb.table.query.DataQueryResult;
import tech.ydb.table.query.Params;
import tech.ydb.table.transaction.TxControl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * @author Kirill Kurdyukov
 */
final class OutTransaction implements YDBConnectionState {

    private final TableClient tableClient;
    private final TxControl<?> txControl;
    private final Duration connectionTimeout;

    OutTransaction(TableClient tableClient, TxControl<?> txControl,  Duration connectionTimeout) {
        this.tableClient = tableClient;
        this.txControl = txControl;
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public CompletableFuture<Result<DataQueryResult>> executeDataQuery(String yql, Params params) {
        return tableClient.createSession(connectionTimeout)
                .thenCompose(
                        sessionResult -> {
                            Session session = ResultExtractor.extract(sessionResult, "Error creating session");

                            return session.executeDataQuery(yql, txControl, params);
                        }
                );
    }
}
