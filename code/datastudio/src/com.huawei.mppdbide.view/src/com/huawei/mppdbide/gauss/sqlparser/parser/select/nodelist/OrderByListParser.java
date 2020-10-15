/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist;

import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumn;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.order.TOrderByItem;

/**
 * 
 * Title: OrderByListParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
