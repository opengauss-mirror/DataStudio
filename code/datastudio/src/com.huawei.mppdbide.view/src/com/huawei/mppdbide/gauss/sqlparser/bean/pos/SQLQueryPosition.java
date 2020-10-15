/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.pos;

/**
 * 
 * Title: SQLQueryPosition
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
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
