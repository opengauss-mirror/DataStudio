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

package org.opengauss.mppdbide.debuger.debug;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Title: the DebugConstants class
 *
 * @since 3.0.0
 */
public class DebugConstants {
    /**
     * default string length
     */
    public static final int DEFAULT_STRING_BUILD_LEN = 128;

    /**
     * default PL_DEBUGGER name
     */
    public static final String PL_DEBUGGER = "pl_debugger";

    /**
     * default DBE_DEBUGGER name
     */
    public static final String DBE_DEBUGGER = "dbe_debugger";

    /**
     * default version
     */
    public static final Integer DBE_DEBUGGER_MIN_VERSION = 300;

    /**
     * enumMap
     */
    public static final LinkedHashMap<String, DebugOpt> ENUM_MAP = getEnumMap(DebugOpt.class);

    /**
     * store dataType
     */
    private static final Map<String, String> DATA_TYPE = new HashMap<>();

    /**
     * dataTypes
     */
    static {
        DATA_TYPE.put("bpchar", "char");
        DATA_TYPE.put("bool", "boolean");
        DATA_TYPE.put("float", "binary double");
        DATA_TYPE.put("int2", "smallint");
        DATA_TYPE.put("int4", "integer");
        DATA_TYPE.put("int8", "bigint");
        DATA_TYPE.put("float8", "double precision");
        DATA_TYPE.put("float4", "real");
        DATA_TYPE.put("timetz", "time with time zone");
        DATA_TYPE.put("timestamptz", "timestamp with time zone");
        DATA_TYPE.put("time", "time without time zone");
        DATA_TYPE.put("bpchar", "timestamp");
    }

    /**
     * getDataType
     *
     * @param data dataType
     * @return the data type string
     */
    public static String getDataType(String data) {
        return DATA_TYPE.get(data);
    }

    /**
     * get DebugOpt by different debugger
     *
     * @param debugOpt        the debugOpt
     * @param debuggerVersion the version
     * @return DebugOpt the return value
     */
    public static DebugOpt getDebugOptByDebuggerVersion(DebugOpt debugOpt, String debuggerVersion) {
        if (PL_DEBUGGER.equalsIgnoreCase(debuggerVersion)) {
            return debugOpt;
        } else {
            String optName = debugOpt.name();
            optName = "DBE_" + optName;
            return ENUM_MAP.get(optName);
        }
    }

    /**
     * get name and all enum map
     *
     * @param <E>
     * @param enumClass the enumclass
     * @return LinkedHashMap
     */
    public static <E extends Enum<E>> LinkedHashMap<String, E> getEnumMap(final Class<E> enumClass) {
        final LinkedHashMap<String, E> map = new LinkedHashMap<>();
        for (final E e : enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }

    /**
     * Title: the DebugOpt enum use to descript debuger interface
     */
    public static enum DebugOpt {
        // pldebugger command
        DEBUG_ON("pldbg_on", 0),
        DEBUG_OFF("pldbg_off", 0),
        DEBUG_VERSION("version", 0),
        GET_SOURCE_CODE("pldbg_get_source", 1),
        GET_TOTAL_SOURCE_CODE("pg_get_functiondef", 1),
        START_SESSION("plpgsql_oid_debug", 1),
        ATTACH_SESSION("pldbg_attach_to_port", 1),
        STEP_INTO("pldbg_step_into", 1),
        STEP_OVER("pldbg_step_over", 1),
        STEP_OUT("pldbg_step_out", 1),
        CONTINUE_EXEC("pldbg_continue", 1),
        ABORT_TARGET("pldbg_abort_target", 1),
        GET_VARIABLES("pldbg_get_variables", 1),
        GET_STACKS("pldbg_get_stack", 1),
        GET_BREAKPOINTS("pldbg_get_breakpoints", 1),
        DROP_BREAKPOINT("pldbg_drop_breakpoint", 3),
        SET_BREAKPOINT("pldbg_set_breakpoint", 3),
        // dbe_debugger command
        DBE_DEBUG_ON("DBE_PLDEBUGGER.turn_on", 1),
        DBE_DEBUG_OFF("DBE_PLDEBUGGER.turn_off", 1),
        DBE_DEBUG_VERSION("version", 0),
        DBE_GET_SOURCE_CODE("DBE_PLDEBUGGER.info_code", 1),
        DBE_GET_TOTAL_SOURCE_CODE("pg_get_functiondef", 1),
        DBE_START_SESSION("DBE_PLDEBUGGER.turn_on", 1),
        DBE_ATTACH_SESSION("DBE_PLDEBUGGER.attach", 2),
        DBE_STEP_INTO("DBE_PLDEBUGGER.step", 1),
        DBE_STEP_OVER("DBE_PLDEBUGGER.next", 0),
        DBE_STEP_OUT("pldbg_step_out", 1),
        DBE_CONTINUE_EXEC("DBE_PLDEBUGGER.continue", 0),
        DBE_ABORT_TARGET("DBE_PLDEBUGGER.abort", 0),
        DBE_GET_VARIABLES("DBE_PLDEBUGGER.print_var", 1),
        DBE_GET_STACKS("DBE_PLDEBUGGER.backtrace", 0),
        DBE_GET_BREAKPOINTS("DBE_PLDEBUGGER.info_breakpoints", 0),
        DBE_DROP_BREAKPOINT("DBE_PLDEBUGGER.delete_breakpoint", 1),
        DBE_SET_BREAKPOINT("DBE_PLDEBUGGER.add_breakpoint", 2),
        DBE_REMARK_INFO_SET("DBE_PLDEBUGGER.remark_info_set", 2),
        DBE_REMARK_INFO_GET("DBE_PLDEBUGGER.remark_info_get", 1),
        DBE_COVERAGE_INFO_GET("DBE_PLDEBUGGER.coverage_info_get", 1),
        DBE_COVERAGE_INFO_DEL("DBE_PLDEBUGGER.coverage_info_del", 2);

        /**
         *  opt of interface
         */
        public final String opt;
        /**
         *  interface param num
         */
        public final int paramNum;

        DebugOpt(String opt, int paramNum) {
            this.opt = opt;
            this.paramNum = paramNum;
        }
    };

    /**
     * get opt sql query
     *
     * @param debugOpt the interface desc
     * @return String query sql
     */
    public static String getSql(DebugOpt debugOpt) {
        return getSql(debugOpt.opt, debugOpt.paramNum);
    }

    /**
     * get opt sql query
     *
     * @param debugOpt the interface desc
     * @param paramNum number of input params number
     * @return String query sql
     */
    public static String getSql(String opt, int paramNum) {
        // generate number of ?
        String paramReplace = Stream.iterate(0, number -> number)
                .limit(paramNum)
                .map(paramArg -> "?")
                .reduce((paramArgsA, paramArgsB) -> paramArgsA + "," +  paramArgsB)
                .orElse("");
        return String.format(Locale.ENGLISH, "select * from %s(%s)",
                opt,
                paramReplace
                );
    }

    /**
     * get openGauss db version sql
     *
     * @return String query sql
     */
    public static String getDbVersionSql() {
        return "select version()";
    }
}
