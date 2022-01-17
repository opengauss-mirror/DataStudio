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

package com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist;

import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumn;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.order.TOrderByItem;

/**
 * 
 * Title: OrderByListParser
 *
 * @since 3.0.0
 */
public class OrderByListParser extends SelectResultListParser {

    /**
     * Instantiates a new order by list parser.
     *
     * @param lineBreakSet the line break set
     */
    public OrderByListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);

    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @return true, if is alias name
     */
    public boolean isAliasName(String nodeStr) {
        return !isInEndNode() && ("AS".equalsIgnoreCase(nodeStr) || "ASC".equalsIgnoreCase(nodeStr)
                || "DESC".equalsIgnoreCase(nodeStr));
    }

    /**
     * Gets the result column.
     *
     * @return the result column
     */
    protected TResultColumn getResultColumn() {
        return new TOrderByItem();
    }

}
