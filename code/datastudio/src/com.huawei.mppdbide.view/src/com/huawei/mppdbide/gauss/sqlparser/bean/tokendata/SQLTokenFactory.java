/*
 * Copyright: Huawei Technologies Co., Ltd. Copyright 2012-2019, All rights reserved
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.tokendata;

/**
 * Title: SQLTokenFactory Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 25-Dec-2019]
 * @since 25-Dec-2019
 */

public abstract class SQLTokenFactory {
    /**
     * factory method to return the token data
     * 
     * @return the sql token data
     */
    public static ISQLTokenData getTokenData() {
        return new SQLTokenData();
    }
}
