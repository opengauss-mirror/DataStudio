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

package org.opengauss.mppdbide.utils.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Execution information after the stored procedure ends
 *
 * @since 3.0.0
 */
public class DebuggerEndInfoVo extends DebuggerStartInfoVo {
    /**
     * cid of function
     */
    public Long cid = System.currentTimeMillis(); // coverage id

    /**
     * endDateLong of function
     */
    public Long endDateLong = System.currentTimeMillis(); // End Time

    /**
     * runList of function
     */
    public List<String> runList; // run to the line

    /**
     * runStr of function
     */
    public String runStr; // the string that runs to the line

    /**
     * get run line
     *
     * @return the return value
     */
    public List<String> getRunList() {
        if (runStr == null || "".equals(runStr)) {
            return new ArrayList();
        } else {
            remarkList = new ArrayList<>(Arrays.asList(runStr.split(",")));
        }
        return remarkList;
    }

    /**
     * set info
     *
     * @param info the info to set
     */
    public void setInfo(DebuggerStartInfoVo info) {
        super.oid = info.oid;
        super.sourceCode = info.sourceCode;
        super.args = info.args;
        super.remarLinesStr = info.remarLinesStr;
    }
}
