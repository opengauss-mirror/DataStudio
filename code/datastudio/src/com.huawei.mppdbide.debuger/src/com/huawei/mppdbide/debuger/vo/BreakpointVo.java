/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.vo;

/**
 * Title: class
 * Description: The Class BreakpointVo.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
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