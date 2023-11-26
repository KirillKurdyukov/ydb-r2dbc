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

package tech.ydb.io.r2dbc.parameter;

import io.r2dbc.spi.Parameter;
import io.r2dbc.spi.R2dbcType;
import tech.ydb.io.r2dbc.type.YDBType;
import tech.ydb.table.values.Value;

import java.util.HashMap;

/**
 * @author Kirill Kurdyukov
 */
public class YDBParameterResolver {

    private static final HashMap<Class<?>, YDBType> CLASS_YDB_TYPE = new HashMap<>();

    static {
        for (YDBType value : YDBType.values()) {
            CLASS_YDB_TYPE.put(value.getJavaType(), value);
        }
    }

    private YDBParameterResolver() {
    }

    public static Value<?> resolve(Object param) {
        if (param instanceof Value<?> value) {
            return value;
        } else if (param instanceof Parameter parameter) {
            return resolveParameter(parameter, param);
        }

        if (CLASS_YDB_TYPE.containsKey(param.getClass())) {
            return CLASS_YDB_TYPE.get(param.getClass())
                    .createValue(param);
        } else {
            throw new RuntimeException(); // TODO correct exception
        }
    }

    private static Value<?> resolveParameter(Parameter parameter, Object param) {
        if (parameter.getType() instanceof R2dbcType r2dbcType) {
            return YDBType.valueOf(r2dbcType).createValue(param);
        } else if (parameter.getType() instanceof YDBType ydbType) {
            return ydbType.createValue(param);
        }

        throw new RuntimeException(); // TODO correct exception
    }
}
