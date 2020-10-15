/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.insert.nodelist;

import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;

/**
 * 
 * Title: InsertIntoListParser
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
public class InsertIntoListParser extends SelectResultListParser {

    /**
     * Instantiates a new insert into list parser.
     *
     * @param lineBreakSet the line break set
     */
    public InsertIntoListParser(Set<String> lineBreakSet) {
        super(lineBreakSet);

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

        return true;
    }

    /**
     * Checks if is list break.
     *
     * @param nodeStr the node str
     * @param parseCount the parse count
     * @return true, if is list break
     */
    @Override
    public boolean isListBreak(String nodeStr, int parseCount) {
        return parseCount == 0 && listBreak.contains(nodeStr.toLowerCase());
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
        // do nothing
    }
}
