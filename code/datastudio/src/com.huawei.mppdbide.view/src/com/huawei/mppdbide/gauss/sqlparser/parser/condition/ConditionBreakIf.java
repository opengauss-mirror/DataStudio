/*
 * Copyright: Huawei Technologies Co., Ltd. Copyright 2012-2019, All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.condition;

import java.util.ListIterator;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;

/**
 * Title: ConditionBreakIf
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 01-Dec-2019]
 * @since 01-Dec-2019
 */

public interface ConditionBreakIf {

    /**
     * Checks if is break condition.
     *
     * @param next the next
     * @param listIterator the list iterator
     * @return true, if is break condition
     */
    boolean isBreakCondition(ISQLTokenData next, ListIterator<ISQLTokenData> listIterator);

}
