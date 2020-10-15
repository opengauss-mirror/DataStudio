/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser;

import org.eclipse.jface.text.rules.Token;

/**
 * 
 * Title: SQLToken
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