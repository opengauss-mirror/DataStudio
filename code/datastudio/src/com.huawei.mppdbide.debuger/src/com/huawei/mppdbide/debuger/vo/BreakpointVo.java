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

package com.huawei.mppdbide.debuger.vo;

/**
 * Title: class
 * Description: The Class BreakpointVo.
 *
 * @since 3.0.0
 */
public class BreakpointVo {
    private int lineNum;
    private String statement;
    private Boolean enable;

    /**
     * Instantiates a new breakpoint.
     *
     * @param lineNum the lineNum
     * @param statement the statement
     * @param enable the enable
     */
    public BreakpointVo(int lineNum, String statement, Boolean enable) {
        this.lineNum = lineNum;
        this.statement = statement;
        this.enable = enable;
    }

    /**
     * Gets the lineNum.
     *
     * @return int the lineNum
     */
    public int getLineNum() {
        return lineNum;
    }

    /**
     * Sets the lineNum.
     *
     * @param lineNum the lineNum
     */
    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    /**
     * Gets the statement.
     *
     * @return String the statement
     */
    public String getStatement() {
        return statement;
    }

    /**
     * Sets the statement.
     *
     * @param statement the statement
     */
    public void setStatement(String statement) {
        this.statement = statement;
    }

    /**
     * Gets the enable.
     *
     * @return Boolean the enable
     */
    public Boolean getEnable() {
        return enable;
    }

    /**
     * Sets the enable.
     *
     * @param enable the enable
     */
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}