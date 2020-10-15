/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.pos;

/**
 * 
 * Title: SQLScriptElement
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
public interface SQLScriptElement {

    /**
     * Gets the original text.
     *
     * @return the original text
     */
    String getOriginalText();

    /**
     * Gets the text.
     *
     * @return the text
     */
    String getText();

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    int getOffset();

    /**
     * Gets the length.
     *
     * @return the length
     */
    int getLength();

    /**
     * Gets the data.
     *
     * @return the data
     */
    Object getData();

    /**
     * Sets the data.
     *
     * @param data the new data
     */
    void setData(Object data);

    /**
     * Reset.
     */
    void reset();
}
