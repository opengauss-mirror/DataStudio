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
 * Stored procedure startup information
 *
 * @since 3.0.0
 */
public class DebuggerStartInfoVo {
    /**
     * oid of function
     */
    public Long oid;

    /**
     * sourceCode of function
     */
    public String sourceCode;

    /**
     * args of function
     */
    public List<?> args;

    /**
     * remarkList of function
     */
    public List<String> remarkList;

    /**
     * remarLinesStr of function
     */
    public String remarLinesStr;

    /**
     * isMakeReport of function
     */
    public boolean isMakeReport = true;

    /**
     * totalCanBreakLine of function
     */
    public String canBreakLine;

    /**
     * get remakr list
     *
     * @return the return value
     */
    public List<String> getRemarkList() {
        if (remarLinesStr == null || "".equals(remarLinesStr)) {
            return new ArrayList();
        } else {
            remarkList = new ArrayList<>(Arrays.asList(remarLinesStr.split(",")));
        }
        return remarkList;
    }
}
