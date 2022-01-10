/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.adapter.keywordssyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class OLAPKeywords.
 * 
 */
public class OLAPKeywords extends Keywords {

    private String[] olapreservedkeywords = {"ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY", "AS", "ASC",
        "ASYMMETRIC", "AUTHID", "AUTHORIZATION", "BINARY", "BOTH", "BUCKETS", "CASE", "CAST", "CHECK", "COLLATE",
        "COLLATION", "COLUMN", "CONCURRENTLY", "CONSTRAINT", "CREATE", "CROSS", "CURRENT_CATALOG", "CURRENT_DATE",
        "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DEFAULT", "DEFERRABLE",
        "DESC", "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "FALSE", "FETCH", "FOR", "FOREIGN", "FREEZE", "FULL",
        "FUNCTION", "GRANT", "GROUP", "HAVING", "ILIKE", "IN", "INITIALLY", "INNER", "INTERSECT", "INTO", "IS",
        "ISNULL", "JOIN", "LEADING", "LEFT", "LESS", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "MAXVALUE",
        "MINUS", "MODIFY", "NATURAL", "NLSSORT", "NOT", "NOTNULL", "NULL", "OFFSET", "ON", "ONLY", "OR", "ORDER",
        "OUTER", "OVER", "OVERLAPS", "PERFORMANCE", "PLACING", "PRIMARY", "PROCEDURE", "REFERENCES", "REJECT", "RETURN",
        "RETURNING", "RIGHT", "SELECT", "SESSION_USER", "SIMILAR", "SOME", "SPLIT", "SYMMETRIC", "SYNONYM", "SYSDATE",
        "TABLE", "THEN", "TO", "TRAILING", "TRUE", "UNION", "UNIQUE", "USER", "USING", "VARIADIC", "VERBOSE", "WHEN",
        "WHERE", "WINDOW", "WITH", "FROM"};

    private String[] olapunreservedkeywords = {"ABORT", "ABSOLUTE", "ACCESS", "ACCOUNT", "ACTION", "ADD", "ADMIN",
        "AFTER", "AGGREGATE", "ALSO", "ALTER", "ALWAYS", "APP", "ASSERTION", "ASSIGNMENT", "AT", "ATTRIBUTE",
        "AUTOEXTEND", "AUTOMAPPED", "BACKWARD", "BARRIER", "BEFORE", "BEGIN", "BETWEEN", "BIGINT", "BINARY_DOUBLE",
        "BINARY_INTEGER", "BIT", "BLOB", "BOOLEAN", "BY", "CACHE", "CALL", "CALLED", "CASCADE", "CASCADED", "CATALOG",
        "CHAIN", "CHAR", "CHARACTER", "CHARACTERISTICS", "CHECKPOINT", "CLASS", "CLEAN", "CLOB", "CLOSE", "CLUSTER",
        "COALESCE", "COMMENT", "COMMENTS", "COMMIT", "COMMITTED", "COMPRESS", "CONFIGURATION", "CONNECTION",
        "CONSTRAINTS", "CONTENT", "CONTINUE", "CONVERSION", "COORDINATOR", "COPY", "COST", "CSV", "CURRENT", "CURSOR",
        "CYCLE", "DATA", "DATABASE", "DATAFILE", "DATE", "DAY", "DBCOMPATIBILITY", "DEALLOCATE", "DEC", "DECIMAL",
        "DECLARE", "DECODE", "DEFAULTS", "DEFERRED", "DEFINER", "DELETE", "DELIMITER", "DELIMITERS", "DELTA",
        "DETERMINISTIC", "DICTIONARY", "DIRECT", "DISABLE", "DISCARD", "DISTRIBUTE", "DISTRIBUTION", "DOCUMENT",
        "DOMAIN", "DOUBLE", "DROP", "EACH", "ENABLE", "ENCODING", "ENCRYPTED", "ENFORCED", "ENUM", "EOL", "ESCAPE",
        "ESCAPING", "EVERY", "EXCHANGE", "EXCLUDE", "EXCLUDING", "EXCLUSIVE", "EXECUTE", "EXISTS", "EXPLAIN",
        "EXTENSION", "EXTERNAL", "EXTRACT", "FAMILY", "FILEHEADER", "FIRST", "FIXED", "FLOAT", "FOLLOWING", "FORCE",
        "FORMATTER", "FORWARD", "FUNCTIONS", "GLOBAL", "GRANTED", "GREATEST", "HANDLER", "HEADER", "HOLD", "HOUR",
        "IDENTIFIED", "IDENTITY", "IF", "IMMEDIATE", "IMMUTABLE", "IMPLICIT", "INCLUDING", "INCREMENT", "INDEX",
        "INDEXES", "INHERIT", "INHERITS", "INITIAL", "INITRANS", "INLINE", "INOUT", "INPUT", "INSENSITIVE", "INSERT",
        "INSTEAD", "INT", "INTEGER", "INTERVAL", "INVOKER", "ISOLATION", "KEY", "LABEL", "LANGUAGE", "LARGE", "LAST",
        "LC_COLLATE", "LC_CTYPE", "LEAKPROOF", "LEAST", "LEVEL", "LISTEN", "LOAD", "LOCAL", "LOCATION", "LOCK", "LOG",
        "LOGGING", "LOGIN", "LOOP", "MAPPING", "MATCH", "MATCHED", "MAXEXTENTS", "MAXSIZE", "MAXTRANS", "MERGE",
        "MINEXTENTS", "MINUTE", "MINVALUE", "MODE", "MONTH", "MOVE", "MOVEMENT", "NAME", "NAMES", "NATIONAL", "NCHAR",
        "NEXT", "NO", "NOCOMPRESS", "NOCYCLE", "NODE", "NOLOGGING", "NOLOGIN", "NOMAXVALUE", "NOMINVALUE", "NONE",
        "NOTHING", "NOTIFY", "NOWAIT", "NULLIF", "NULLS", "NUMBER", "NUMERIC", "NUMSTR", "NVARCHAR2", "NVL", "OBJECT",
        "OF", "OFF", "OIDS", "OPERATOR", "OPTIMIZATION", "OPTION", "OPTIONS", "OUT", "OVERLAY", "OWNED", "OWNER",
        "PARSER", "PARTIAL", "PARTITION", "PARTITIONS", "PASSING", "PASSWORD", "PCTFREE", "PER", "PERCENT", "PLANS",
        "POOL", "POSITION", "PRECEDING", "PRECISION", "PREFERRED", "PREFIX", "PREPARE", "PREPARED", "PRESERVE", "PRIOR",
        "PRIVILEGE", "PRIVILEGES", "PROCEDURAL", "PROFILE", "QUERY", "QUOTE", "RANGE", "RAW", "READ", "REAL",
        "REASSIGN", "REBUILD", "RECHECK", "RECURSIVE", "REF", "REINDEX", "RELATIVE", "RELEASE", "RELOPTIONS", "REMOTE",
        "RENAME", "REPEATABLE", "REPLACE", "REPLICA", "RESET", "RESIZE", "RESOURCE", "RESTART", "RESTRICT", "RETURNS",
        "REUSE", "REVOKE", "ROLE", "ROLLBACK", "ROW", "ROWS", "RULE", "SAVEPOINT", "SCHEMA", "SCROLL", "SEARCH",
        "SECOND", "SECURITY", "SEQUENCE", "SEQUENCES", "SERIALIZABLE", "SERVER", "SESSION", "SET", "SETOF", "SHARE",
        "SHOW", "SIMPLE", "SIZE", "SMALLDATETIME", "SMALLINT", "SNAPSHOT", "STABLE", "STANDALONE", "START", "STATEMENT",
        "STATISTICS", "STDIN", "STDOUT", "STORAGE", "STORE", "STRICT", "STRIP", "SUBSTRING", "SUPERUSER",
        "SYS_REFCURSOR", "SYSID", "SYSTEM", "TABLES", "TABLESPACE", "TEMP", "TEMPLATE", "TEMPORARY", "TEXT", "THAN",
        "TIME", "TIMESTAMP", "TINYINT", "TRANSACTION", "TREAT", "TRIGGER", "TRIM", "TRUNCATE", "TRUSTED", "TYPE",
        "TYPES", "UNBOUNDED", "UNCOMMITTED", "UNENCRYPTED", "UNKNOWN", "UNLIMITED", "UNLISTEN", "UNLOCK", "UNLOGGED",
        "UNTIL", "UNUSABLE", "UPDATE", "VACUUM", "VALID", "VALIDATE", "VALIDATION", "VALIDATOR", "VALUE", "VALUES",
        "VARCHAR", "VARCHAR2", "VARYING", "VERSION", "VIEW", "VOLATILE", "WHITESPACE", "WITHOUT", "WORK", "WORKLOAD",
        "WRAPPER", "WRITE", "XML", "XMLATTRIBUTES", "XMLCONCAT", "XMLELEMENT", "XMLEXISTS", "XMLFOREST", "XMLPARSE",
        "XMLPI", "XMLROOT", "XMLSERIALIZE", "YEAR", "YES", "ZONE"};

    private String[] olaptypes = {"TINYINT", "SMALLINT", "INTEGER", "BIGINT", "NUMERIC", "DECIMAL", "NUMBER",
        "SMALLSERIAL", "SERIAL", "BIGSERIAL", "REAL", "FLOAT4", "DOUBLEPRECISION", "FLOAT8", "FLOAT", "DEC", "INTEGER",
        "money", "BOOLEAN", " VARCHAR", "CHARACTER", "VARYING", "CHAR", "CHARACTER", "inet", "macaddr", "bit",
        "tsvector", "tsquery", "UUID", "JSON", "NCHAR", "VARCHAR2", "NVARCHAR2", "CLOB", "TEXT", "BLOB", "RAW", "BYTEA",
        "DATE", "TIME", "WITHOUT", "ZONE", "WITH", "TIMESTAMP", "SMALLDATETIME", "INTERVAL", "DAY", "SECOND",
        "INTERVAL", "reltime", "point", "lseg", "box", "path", "polygon", "circle", "cidr"};

    @Override
    public String[] getReservedKeywords() {
        return olapreservedkeywords.clone();
    }

    @Override
    public String[] getUnReservedKeywords() {
        return olapunreservedkeywords.clone();
    }

    @Override
    public String[] getTypes() {
        return olaptypes.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
