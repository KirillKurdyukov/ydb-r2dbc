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

import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import reactor.core.publisher.Mono;
import tech.ydb.io.r2dbc.state.YDBConnectionState;
import tech.ydb.io.r2dbc.parameter.YDBParameterResolver;
import tech.ydb.table.query.Params;

/**
 * @author Kirill Kurdyukov
 */
public class YDBStatement implements Statement {
    private final String sql; // YQL Dialect
    private final YDBConnectionState state;
    private final Params params;

    public YDBStatement(YDBConnectionState state, String sql) {
        this.sql = sql;
        this.state = state;
        this.params = Params.create();
    }

    @Override
    public Statement add() {
        throw new UnsupportedOperationException("Unsupported batch params");
    }

    @Override
    public Statement bind(int index, Object value) {
        throw new UnsupportedOperationException("Use parameter binding by name");
    }

    @Override
    public Statement bind(String name, Object value) {
        params.put(String.format("$%s", name), YDBParameterResolver.resolve(value));
        return this;
    }

    @Override
    public Statement bindNull(int index, Class<?> type) {
        throw new UnsupportedOperationException("Use parameter binding by name");
    }

    @Override
    public Statement bindNull(String name, Class<?> type) {
        throw new UnsupportedOperationException("Use NULL in query");
    }

    @Override
    public Mono<? extends Result> execute() {
        return Mono.fromFuture(state.executeDataQuery(sql, params)).map(YDBResult::new);
    }
}
