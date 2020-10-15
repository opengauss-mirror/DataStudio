/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser;

/**
 * 
 * Title: SQLDDLToken
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
public class SQLDDLToken extends SQLToken {

    /**
     * Instantiates a new SQLDDL token.
     *
     * @param tokenType the token type
     * @param data the data
     */
    public SQLDDLToken(int tokenType, Object data) {
        super(tokenType, data);
    }
}
