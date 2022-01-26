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

package com.huawei.mppdbide.gauss.sqlparser.parser.condition;

import java.util.ListIterator;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;

/**
 * Title: ConditionBreakIf
 * 
 * @since 3.0.0
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
