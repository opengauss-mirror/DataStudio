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
 * Title: SQLScriptElement
 *
 * @since 3.0.0
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
