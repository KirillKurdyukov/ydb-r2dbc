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
import io.r2dbc.spi.Result;
import org.reactivestreams.Publisher;
import tech.ydb.io.r2dbc.state.YDBConnectionState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kirill Kurdyukov
 */
public final class YDBBatch implements Batch {

    private final YDBConnectionState state;
    private final List<String> statements = new ArrayList<>();

    public YDBBatch(YDBConnectionState state) {
        this.state = state;
    }

    @Override
    public Batch add(String sql) {
        statements.add(sql);

        return this;
    }

    @Override
    public Publisher<? extends Result> execute() {
        return new YDBStatement(state, String.join(";", statements)).execute();
    }
}
