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

package com.huawei.mppdbide.gauss.sqlparser;

import org.eclipse.jface.text.rules.Token;

/**
 * 
 * Title: SQLToken
 *
 * @since 3.0.0
 */
public class SQLToken extends Token {

    private final int type;

    private boolean isNested = true;

    /**
     * Instantiates a new SQL token.
     *
     * @param type the type
     * @param data the data
     */
    public SQLToken(int type, Object data) {
        super(data);
        this.type = type;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Checks if is nested.
     *
     * @return true, if is nested
     */
    public boolean isNested() {
        return isNested;
    }

    /**
     * Sets the nested.
     *
     * @param isNested the new nested
     */
    public void setNested(boolean isNested) {
        this.isNested = isNested;
    }

}