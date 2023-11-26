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

import com.google.common.base.Preconditions;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactoryProvider;
import io.r2dbc.spi.Option;
import tech.ydb.core.grpc.BalancingSettings;
import tech.ydb.core.grpc.GrpcCompression;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.core.grpc.GrpcTransportBuilder;
import tech.ydb.table.TableClient;

/**
 * @author Kirill Kurdyukov
 */
public final class YDBConnectionFactoryProvider implements ConnectionFactoryProvider {

    private static final String YDB_DRIVER = "ydb";

    private static final int SESSION_POOL_DEFAULT_MIN_SIZE = 10;
    private static final int SESSION_POOL_DEFAULT_MAX_SIZE = 100;

    private static final Option<Integer> SESSION_POOL_MIN_SIZE = Option.valueOf("sessionPoolMinSize");
    private static final Option<Integer> SESSION_POOL_MAX_SIZE = Option.valueOf("sessionPoolMaxSize");
    private static final Option<GrpcCompression> GRPC_COMPRESSION = Option.valueOf("grpcCompression");
    private static final Option<BalancingSettings.Policy> BALANCING_POLICY = Option.valueOf("balancingPolicy");
    private static final Option<byte[]> SECURE_CONNECTION_CERTIFICATE = Option.valueOf("secureConnectionCertificate");
    private static final Option<Boolean> SECURE_CONNECTION = Option.valueOf("secureConnection");
    private static final Option<String> SA_FILE = Option.valueOf("saFile"); // TODO

    @Override
    public YDBConnectionFactory create(ConnectionFactoryOptions connectionFactoryOptions) {
        OptionExtractor optionExtractor = new OptionExtractor(connectionFactoryOptions);

        String schema = optionExtractor.extract(ConnectionFactoryOptions.PROTOCOL);

        GrpcTransportBuilder grpcTransportBuilder = GrpcTransport.forHost(
                (schema == null ? "" : schema + "://") + optionExtractor.extractRequired(ConnectionFactoryOptions.HOST),
                optionExtractor.extractRequired(ConnectionFactoryOptions.PORT),
                optionExtractor.extractRequired(ConnectionFactoryOptions.DATABASE)
        );

        optionExtractor.extractThenConsume(BALANCING_POLICY, policy -> grpcTransportBuilder
                .withBalancingSettings(BalancingSettings.fromPolicy(policy)));
        optionExtractor.extractThenConsume(GRPC_COMPRESSION, grpcTransportBuilder::withGrpcCompression);
        optionExtractor.extractThenConsume(SECURE_CONNECTION_CERTIFICATE, grpcTransportBuilder::withSecureConnection);
        optionExtractor.extractThenConsume(SECURE_CONNECTION, isSecure -> {
            if (isSecure) {
                grpcTransportBuilder.withSecureConnection();
            }
        });

        TableClient tableClient = TableClient.newClient(grpcTransportBuilder.build())
                .sessionPoolSize(
                        optionExtractor.extractOrDefault(SESSION_POOL_MIN_SIZE, SESSION_POOL_DEFAULT_MIN_SIZE),
                        optionExtractor.extractOrDefault(SESSION_POOL_MAX_SIZE, SESSION_POOL_DEFAULT_MAX_SIZE)
                )
                .build();

        return new YDBConnectionFactory(tableClient);
    }

    @Override
    public boolean supports(ConnectionFactoryOptions connectionFactoryOptions) {
        Preconditions.checkNotNull(connectionFactoryOptions, "connectionFactoryOptions must not be null");

        Object driver = connectionFactoryOptions.getValue(ConnectionFactoryOptions.DRIVER);

        return driver != null && driver.equals(YDB_DRIVER);
    }

    @Override
    public String getDriver() {
        return YDB_DRIVER;
    }
}
