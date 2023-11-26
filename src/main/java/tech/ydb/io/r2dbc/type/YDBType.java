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

package tech.ydb.io.r2dbc.type;

import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Type;
import tech.ydb.table.values.DecimalType;
import tech.ydb.table.values.PrimitiveValue;
import tech.ydb.table.values.Value;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Kirill Kurdyukov
 */
public enum YDBType implements Type {

    /**
     * Boolean value
     */
    BOOL(Boolean.class, (obj) -> PrimitiveValue.newBool((Boolean) obj)),

    /**
     * A signed integer. Acceptable values: from -2^7 to 2^7–1.
     * Not supported for table columns
     */
    INT8(Byte.class, (obj) -> PrimitiveValue.newInt8((Byte) obj)),

    /**
     * A signed integer. Acceptable values: from –2^15 to 2^15–1.
     * Not supported for table columns
     */
    INT16(Short.class, (obj) -> PrimitiveValue.newInt16((Short) obj)),

    /**
     * A signed integer. Acceptable values: from –2^31 to 2^31–1.
     */
    INT32(Integer.class, (obj) -> PrimitiveValue.newInt32((Integer) obj)),

    /**
     * A signed integer. Acceptable values: from –2^63 to 2^63–1.
     */
    INT64(Long.class, (obj) -> PrimitiveValue.newInt64((Long) obj)),

    /**
     * A real number with variable precision, 4 bytes in size.
     * Can't be used in the primary key
     */
    FLOAT(Float.class, (obj) -> PrimitiveValue.newFloat((Float) obj)),

    /**
     * A real number with variable precision, 8 bytes in size.
     * Can't be used in the primary key
     */
    DOUBLE(Double.class, (obj) -> PrimitiveValue.newDouble((Double) obj)),

    /**
     * A binary data, synonym for YDB type String
     */
    BYTES(byte[].class, (obj) -> PrimitiveValue.newBytes((byte[]) obj)),

    /**
     * Text encoded in UTF-8, synonym for YDB type Utf8
     */
    TEXT(String.class, (obj) -> PrimitiveValue.newText((String) obj)),

    /**
     * YSON in a textual or binary representation.
     * Doesn't support matching, can't be used in the primary key
     */
    YSON(byte[].class, (obj) -> PrimitiveValue.newYson((byte[]) obj)),

    /**
     * JSON represented as text. Doesn't support matching, can't be used in the primary key
     */
    JSON(String.class, (obj) -> PrimitiveValue.newText((String) obj)),

    /**
     * Universally unique identifier UUID. Not supported for table columns
     */
    UUID(UUID.class, (obj) -> PrimitiveValue.newUuid((UUID) obj)),

    /**
     * Date, precision to the day
     */
    DATE(LocalDate.class, obj -> PrimitiveValue.newDate((LocalDate) obj)),

    /**
     * Date/time, precision to the second
     */
    DATETIME(Instant.class, obj -> PrimitiveValue.newDatetime((Instant) obj)),

    /**
     * Date/time, precision to the microsecond
     */
    TIMESTAMP(Instant.class, obj -> PrimitiveValue.newTimestamp((Instant) obj)),

    /**
     * Time interval (signed), precision to microseconds
     */
    INTERVAL(Duration.class, obj -> PrimitiveValue.newInterval((Duration) obj)),

    /**
     * Date with time zone label, precision to the day
     */
    TZ_DATE(ZonedDateTime.class, obj -> PrimitiveValue.newTzDate((ZonedDateTime) obj)),

    /**
     * Date/time with time zone label, precision to the second
     */
    TZ_DATETIME(ZonedDateTime.class, obj -> PrimitiveValue.newTzDatetime((ZonedDateTime) obj)),

    /**
     * Date/time with time zone label, precision to the microsecond
     */
    TZ_TIMESTAMP(ZonedDateTime.class, obj -> PrimitiveValue.newTzTimestamp((ZonedDateTime) obj)),

    /**
     * JSON in an indexed binary representation.
     * Doesn't support matching, can't be used in the primary key
     */
    JSON_DOCUMENT(String.class, obj -> PrimitiveValue.newJsonDocument((String) obj)),

    /**
     * A real number with the specified precision, up to 35 decimal digits.
     * <p>
     * When used in table columns, precision is fixed: Decimal (22,9).
     * Can't be used in the primary key
     */
    DECIMAL(BigDecimal.class, obj -> DecimalType.getDefault().newValue((BigDecimal) obj))

    ;

    private final Class<?> javaType;
    private final Function<Object, Value<?>> valueConstructor;

    YDBType(Class<?> javaType, Function<Object, Value<?>> valueConstructor) {
        this.javaType = javaType;
        this.valueConstructor = valueConstructor;
    }

    @Override
    public Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public String getName() {
        return name();
    }

    public Value<?> createValue(Object obj) {
        return valueConstructor.apply(obj);
    }

    public static YDBType valueOf(R2dbcType r2dbcType) {
        return switch (r2dbcType) {
            case BOOLEAN -> BOOL;
            case TINYINT -> INT8;
            case SMALLINT -> INT16;
            case INTEGER -> INT32;
            case BIGINT -> INT64;
            case CHAR, VARCHAR, NCHAR, NVARCHAR, CLOB, NCLOB -> TEXT;
            case REAL, FLOAT -> FLOAT;
            case DOUBLE -> DOUBLE;
            case BINARY, VARBINARY, BLOB -> BYTES;
            case DATE -> DATE;
            case TIME -> DATETIME;
            case TIMESTAMP -> TIMESTAMP;
            case TIMESTAMP_WITH_TIME_ZONE -> TZ_TIMESTAMP;
            case TIME_WITH_TIME_ZONE -> TZ_DATETIME;
            case NUMERIC, DECIMAL -> DECIMAL;
            case COLLECTION -> throw new UnsupportedOperationException("TODO Support");
        };
    }
}
