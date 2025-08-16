package com.smg.sqlparser.enums;

import lombok.Getter;

/**
 * Enum representing standard SQL:1999 data types.
 * Some types include optional default length, precision, or scale metadata.
 */
@Getter
public enum DataType {
    // Character types
    CHAR(1, null, null),
    VARCHAR(255, null, null),
    CLOB(null, null, null),
    BLOB(null, null, null),
    // Numeric types
    NUMERIC(null, 10, 0),
    DECIMAL(null, 10, 0),
    INT(null, null, null),
    SMALLINT(null, null, null),
    BIGINT(null, null, null),
    FLOAT(null, 53, null),
    REAL(null, 24, null),
    DOUBLE_PRECISION(null, 53, null),
    // Boolean type
    BOOLEAN(null, null, null),
    // Date/time types
    DATE(null, null, null),
    TIME(null, null, null),
    TIMESTAMP(null, null, null),
    INTERVAL(null, null, null);

    private final Integer defaultLength;
    private final Integer defaultPrecision;
    private final Integer defaultScale;

    /**
     * Constructor for SQL:1999 type with optional default length, precision, and scale.
     *
     * @param defaultLength    Default length for character types, or null if not applicable.
     * @param defaultPrecision Default precision for numeric types, or null if not applicable.
     * @param defaultScale     Default scale for numeric types, or null if not applicable.
     */
    DataType(Integer defaultLength, Integer defaultPrecision, Integer defaultScale) {
        this.defaultLength = defaultLength;
        this.defaultPrecision = defaultPrecision;
        this.defaultScale = defaultScale;
    }
}
