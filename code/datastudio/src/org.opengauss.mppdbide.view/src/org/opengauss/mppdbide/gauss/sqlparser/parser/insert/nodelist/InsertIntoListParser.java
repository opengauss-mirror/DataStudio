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

package org.opengauss.mppdbide.gauss.sqlparser.parser.insert.nodelist;

import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import org.opengauss.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;

/**
 * 
 * Title: InsertIntoListParser
 *
 * @since 3.0.0
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
