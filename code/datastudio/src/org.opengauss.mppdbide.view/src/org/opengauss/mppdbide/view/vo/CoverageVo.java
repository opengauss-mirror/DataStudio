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

package org.opengauss.mppdbide.view.vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.opengauss.mppdbide.debuger.annotation.DumpFiled;

/**
 * CoverageVo
 *
 * @since 3.0.0
 */
public class CoverageVo {
    /**
     * stored procedure key
     */
    @DumpFiled
    public long oid;

    /**
     * coverage report key
     */
    @DumpFiled
    public long cid;

    /**
     * Stored procedure total number of rows
     */
    public int totalLineNum;

    /**
     * The number of executed statements
     */
    public int coverageLineNum;

    /**
     * The line number of the executed statement
     */
    @DumpFiled
    public String coverageLines;

    /**
     * Set of executed statement line numbers
     */
    public List<String> coverageLinesArr;

    /**
     * total coverage
     */
    public String totalPercent;

    /**
     * number of lines marked
     */
    public int remarkLineNum;

    /**
     * marked line number
     */
    @DumpFiled
    public String remarkLines;

    /**
     * collection of marked line numbers
     */
    public List<String> remarkLinesArr;

    /**
     * Mark the number of lines executed
     */
    public int remarkCoverageLineNum;

    /**
     * The set of line numbers to which the flag is executed
     */
    public List<String> remarkCoverageLinesArr;

    /**
     * markup coverage
     */
    public String remarkPercent;

    /**
     * End Time
     */
    @DumpFiled
    public long endTime;

    /**
     * execute statement
     */
    @DumpFiled
    public String sourceCode;

    /**
     * the params
     */
    @DumpFiled
    public String params;

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * Get the remark list
     *
     * @return List<String>
     */
    public List<String> getRemarkList() {
        if (remarkLines == null || "".equals(remarkLines)) {
            return new ArrayList();
        } else {
            remarkLinesArr = new ArrayList<>(Arrays.asList(remarkLines.split(",")));
        }
        return remarkLinesArr;
    }

    /**
     * Get the run list
     *
     * @return List<String>
     */
    public List<String> getRunList() {
        if (coverageLines == null || "".equals(coverageLines)) {
            return new ArrayList();
        } else {
            coverageLinesArr = new ArrayList<>(Arrays.asList(coverageLines.split(",")));
        }
        return coverageLinesArr;
    }

    /**
     * parseDate
     *
     * @return String
     */
    public String parseDate() {
        Date date = new Date(this.endTime);
        return df.format(date);
    }

    /**
     * getEndTime
     *
     * @return String
     */
    public long getEndTime() {
        return endTime;
    }
}
