/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.debug;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Title: the DebugConstants class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/16]
 * @since 2020/11/16
 */
public class DebugConstants {
    /**
     * default string length
     */
    public static final int DEFAULT_STRING_BUILD_LEN = 128;

    /**
     * Title: the DebugOpt enum use to descript debuger interface
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author z00588921
     * @version [DataStudio 1.0.0, 2020/11/16]
     * @since 2020/11/16
     */
    public static enum DebugOpt {
        DEBUG_ON("pldbg_on", 0),
        DEBUG_OFF("pldbg_off", 0),
        DEBUG_VERSION("pldbg_get_proxy_info", 0),
        GET_SOURCE_CODE("pldbg_get_source", 1),
        GET_TOTAL_SOURCE_CODE("pg_get_functiondef", 1),
        START_SESSION("plpgsql_oid_debug", 1),
        ATTACH_SESSION("pldbg_attach_to_port", 1),
        STEP_INTO("pldbg_step_into", 1),
        STEP_OVER("pldbg_step_over", 1),
        CONTINUE_EXEC("pldbg_continue", 1),
        ABORT_TARGET("pldbg_abort_target", 1),
        GET_VARIABLES("pldbg_get_variables", 1),
        GET_STACKS("pldbg_get_stack", 1),
        GET_BREAKPOINTS("pldbg_get_breakpoints", 1),
        DROP_BREAKPOINT("pldbg_drop_breakpoint", 3),
        SET_BREAKPOINT("pldbg_set_breakpoint", 3);

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
}
