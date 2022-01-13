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

package com.huawei.mppdbide.view.ui;

/**
 * 
 * Title: class
 * 
 * Description: The Class QueryInfo.
 *
 * @since 3.0.0
 */
public final class QueryInfo {

    private int startLine;
    private int endLine;
    private int startOffset;
    private int endOffset;
    private int errorPosition;
    private int errLineNo;
    private String errorMsgString;
    private String serverMessageString;
    private String query;

    /**
     * Instantiates a new query info.
     */
    public QueryInfo() {
        this.startLine = 0;
        this.endLine = 0;
        this.startOffset = 0;
        this.endOffset = 0;
        this.errorPosition = 0;
        this.errLineNo = 0;
        this.errorMsgString = null;

    }

    /**
     * Gets the start line.
     *
     * @return the start line
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Sets the start line.
     *
     * @param startLine the new start line
     */
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    /**
     * Gets the end line.
     *
     * @return the end line
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Sets the end line.
     *
     * @param endLine the new end line
     */
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    /**
     * Gets the start offset.
     *
     * @return the start offset
     */
    public int getStartOffset() {
        return startOffset;
    }

    /**
     * Sets the start offset.
     *
     * @param startOffset the new start offset
     */
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    /**
     * Gets the end offset.
     *
     * @return the end offset
     */
    public int getEndOffset() {
        return endOffset;
    }

    /**
     * Sets the end offset.
     *
     * @param endOffset the new end offset
     */
    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    /**
     * Gets the error msg string.
     *
     * @return the error msg string
     */
    public String getErrorMsgString() {
        return errorMsgString;
    }

    /**
     * Sets the error msg string.
     *
     * @param errorMsgString the new error msg string
     */
    public void setErrorMsgString(String errorMsgString) {
        this.errorMsgString = errorMsgString;
    }

    /**
     * Gets the error position.
     *
     * @return the error position
     */
    public int getErrorPosition() {
        return errorPosition;
    }

    /**
     * Sets the error position.
     *
     * @param errorPosition the new error position
     */
    public void setErrorPosition(int errorPosition) {
        this.errorPosition = errorPosition;
    }

    /**
     * Gets the err line no.
     *
     * @return the err line no
     */
    public int getErrLineNo() {
        return errLineNo;
    }

    /**
     * Sets the err line no.
     *
     * @param errLineNo the new err line no
     */
    public void setErrLineNo(int errLineNo) {
        this.errLineNo = errLineNo;
    }

    /**
     * Gets the server message string.
     *
     * @return the server message string
     */
    public String getServerMessageString() {
        return serverMessageString;
    }

    /**
     * Sets the server message string.
     *
     * @param serverMessageString the new server message string
     */
    public void setServerMessageString(String serverMessageString) {
        this.serverMessageString = serverMessageString;
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query.
     *
     * @param query the new query
     */
    public void setQuery(String query) {
        this.query = query;
    }
}
