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

package com.huawei.mppdbide.debuger.debug;

import java.util.Locale;
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
     * Title: the DebugOpt enum use to descript debuger interface
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
        STEP_OUT("pldbg_step_out", 1),
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
