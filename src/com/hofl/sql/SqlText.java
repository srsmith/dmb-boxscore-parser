package com.hofl.sql;

/**
 * Escaping for string literals embedded in hand-built SQL (this codebase concatenates
 * SQL rather than using PreparedStatement parameter binding). Deliberately not part of
 * DBUtils: DBUtils's static field initializer eagerly loads application.properties and
 * throws if it's missing, so referencing that class anywhere -- including from a plain
 * string-escaping helper -- would fail outside an environment where that file is on
 * the classpath.
 */
public class SqlText {

    private SqlText() {
    }

    /**
     * Escapes a value for use inside a single-quoted SQL string literal under MySQL's
     * default backslash-escape mode. Backslashes must be escaped first: escaping only
     * quotes (as this codebase previously did) leaves a value ending in a raw
     * backslash free to consume the literal's closing quote, corrupting the rest of
     * the statement.
     */
    public static String escape(String value) {
        if (value == null) {
            return value;
        }
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }
}
