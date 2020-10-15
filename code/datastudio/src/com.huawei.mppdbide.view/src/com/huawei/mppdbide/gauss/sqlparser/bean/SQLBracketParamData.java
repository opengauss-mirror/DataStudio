/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean;

/**
 * 
 * Title: SQLBracketParamData
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
public class SQLBracketParamData {

    /**
     * The start end token counter.
     */
    int startEndTokenCounter = 0;

    /**
     * Gets the start end token counter.
     *
     * @return the start end token counter
     */
    public int getStartEndTokenCounter() {
        return startEndTokenCounter;
    }

    /**
     * Incr start end token counter.
     */
    public void incrStartEndTokenCounter() {
        this.startEndTokenCounter++;
    }

    /**
     * Decr start end token counter.
     */
    public void decrStartEndTokenCounter() {
        this.startEndTokenCounter--;
    }

}
