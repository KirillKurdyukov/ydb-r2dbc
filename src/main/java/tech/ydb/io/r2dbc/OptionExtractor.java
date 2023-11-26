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

import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import reactor.util.annotation.Nullable;

import java.util.function.Consumer;

/**
 * @author Kirill Kurdyukov
 */
final class OptionExtractor {

    private final ConnectionFactoryOptions connectionFactoryOptions;

    OptionExtractor(ConnectionFactoryOptions connectionFactoryOptions) {
        this.connectionFactoryOptions = connectionFactoryOptions;
    }

    @Nullable
    <T> T extract(Option<T> option) {
        if (connectionFactoryOptions.hasOption(option)) {
            return extractRequired(option);
        }

        return null;
    }

    <T> T extractOrDefault(Option<T> option, T defaultValue) {
        if (connectionFactoryOptions.hasOption(option)) {
            return extractRequired(option);
        }

        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    <T> T extractRequired(Option<T> option) {
        return (T) connectionFactoryOptions.getRequiredValue(option);
    }

    <T> void extractThenConsume(Option<T> option, Consumer<T> consumer) {
        if (connectionFactoryOptions.hasOption(option)) {
            consumer.accept(extractRequired(option));
        }
    }
}
