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

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import reactor.core.publisher.Mono;
import tech.ydb.table.TableClient;

/**
 * @author Kirill Kurdyukov
 */
public final class YDBConnectionFactory implements ConnectionFactory {

    private final TableClient tableClient;

    public YDBConnectionFactory(TableClient tableClient) {
        this.tableClient = tableClient;
    }

    @Override
    public Mono<? extends Connection> create() {
        return Mono.just(new YDBConnection(tableClient));
    }

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        return YDBConnectionFactoryMetadata.INSTANCE;
    }
}
