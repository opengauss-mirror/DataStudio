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

package org.opengauss.mppdbide.gauss.sqlparser.bean.pos;

/**
 * 
 * Title: SQLQueryPosition
 *
 * @since 3.0.0
 */
public class SQLQueryPosition implements SQLScriptElement {

    private int offset;
    private int length;

    /**
     * Instantiates a new SQL query position.
     *
     * @param lOffSet the l off set
     * @param llength the llength
     */
    public SQLQueryPosition(int lOffSet, int llength) {
        this.offset = lOffSet;
        this.length = llength;

    }

    /**
     * Gets the original text.
     *
     * @return the original text
     */
    @Override
    public String getOriginalText() {
        return null;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    @Override
    public String getText() {
        return null;
    }

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    @Override
    public int getOffset() {

        return offset;
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    @Override
    public int getLength() {
        return length;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    @Override
    public Object getData() {
        return null;
    }

    /**
     * Sets the data.
     *
     * @param data the new data
     */
    @Override
    public void setData(Object data) {

    }

    /**
     * Reset.
     */
    @Override
    public void reset() {

    }

}
