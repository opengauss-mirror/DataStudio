/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.with.nodelist;

import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumn;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.with.TWithItem;

/**
 * 
 * Title: WithItemListParser
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
public class WithItemListParser extends SelectResultListParser {

    /**
     * Instantiates a new with item list parser.
     *
     * @param lineBreakSet the line break set
     */
    public WithItemListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);

    }

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @return true, if is alias name
     */
    public boolean isAliasName(String nodeStr) {
        return !isInEndNode() && "AS".equalsIgnoreCase(nodeStr);
    }

    /**
     * Gets the result column.
     *
     * @return the result column
     */
    protected TResultColumn getResultColumn() {
        return new TWithItem();
    }

    /**
     * Checks if is list break with custom SQL.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param sqlStmtTokenListBean the sql stmt token list bean
     * @return true, if is list break with custom SQL
     */
    @Override
    public boolean isListBreakWithCustomSQL(String previousNotEmptyToken, SQLStmtTokenListBean sqlStmtTokenListBean) {

        if (")".equalsIgnoreCase(previousNotEmptyToken)) {
            return true;
        }

        return false;
    }

    /**
     * Handle start end node.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param next the next
     * @param listIterator the list iterator
     */
    public void handleStartEndNode(String previousNotEmptyToken, ISQLTokenData next,
            ListIterator<ISQLTokenData> listIterator) {
        // do nothing in case of with
    }

}
