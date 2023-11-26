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
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.ydb.core.UnexpectedResultException;
import tech.ydb.table.query.DataQueryResult;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Kirill Kurdyukov
 */
public final class YDBResult implements Result {

    private final DataQueryResult dataQueryResult;

    public YDBResult(tech.ydb.core.Result<DataQueryResult> dataQueryResult) {
        if (dataQueryResult.isSuccess()) {
            this.dataQueryResult = dataQueryResult.getValue();
        } else {
            throw new UnexpectedResultException("Error result query", dataQueryResult.getStatus());
        }
    }

    @Override
    public Publisher<Integer> getRowsUpdated() {
        return Mono.just(0); // Count rows updated is unsupported :(
    }

    @Override
    public <T> Flux<T> map(BiFunction<Row, RowMetadata, ? extends T> mappingFunction) {
        var resultRows = new ArrayList<T>();

        for (int i = 0; i < dataQueryResult.getResultSetCount(); i++) {
            var currentResultSet = dataQueryResult.getResultSet(i);

            resultRows.add(mappingFunction.apply(new YDBRow(currentResultSet), new YDBRowMetadata(currentResultSet)));
        }

        return Flux.fromIterable(resultRows);
    }

    @Override
    public Result filter(Predicate<Segment> filter) {
        return this;
    }

    @Override
    public <T> Publisher<T> flatMap(Function<Segment, ? extends Publisher<? extends T>> mappingFunction) {
        return null;
    }
}
