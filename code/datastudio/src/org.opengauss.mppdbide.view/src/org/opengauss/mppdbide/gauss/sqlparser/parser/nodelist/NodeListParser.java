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

package org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist;

import java.util.ListIterator;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: NodeListParser
 *
 * @since 3.0.0
 */
public interface NodeListParser {

    /**
     * Creates the T object.
     */
    void createTObject();

    /**
     * Handle node alias.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    void handleNodeAlias(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next);

    /**
     * Handle node end.
     *
     * @param listIterator the list iterator
     * @param next the next
     */
    void handleNodeEnd(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next);

    /**
     * Creates the T node.
     *
     * @param listIterator the list iterator
     * @param next the next
     * @param paramCount the param count
     */
    void createTNode(ListIterator<ISQLTokenData> listIterator, ISQLTokenData next, int paramCount);

    /**
     * Creates the T custom stmt node.
     *
     * @param customSqlStmt the custom sql stmt
     * @param listIterator the list iterator
     * @param next the next
     */
    void createTCustomStmtNode(TParseTreeNode customSqlStmt, ListIterator<ISQLTokenData> listIterator,
            ISQLTokenData next);

    /**
     * Checks if is node end.
     *
     * @param nodeStr the node str
     * @return true, if is node end
     */
    boolean isNodeEnd(String nodeStr);

    /**
     * Checks if is alias name.
     *
     * @param nodeStr the node str
     * @param previousNotEmptyToken the previous not empty token
     * @return true, if is alias name
     */
    boolean isAliasName(String nodeStr, String previousNotEmptyToken);

    /**
     * Checks if is list break.
     *
     * @param nodeStr the node str
     * @param paramCount the param count
     * @return true, if is list break
     */
    boolean isListBreak(String nodeStr, int paramCount);

    /**
     * Handle start end node.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param next the next
     * @param listIterator the list iterator
     */
    void handleStartEndNode(String previousNotEmptyToken, ISQLTokenData next, ListIterator<ISQLTokenData> listIterator);

    /**
     * Checks if is list break with custom SQL.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param sqlStmtTokenListBean the sql stmt token list bean
     * @return true, if is list break with custom SQL
     */
    boolean isListBreakWithCustomSQL(String previousNotEmptyToken, SQLStmtTokenListBean sqlStmtTokenListBean);

    /**
     * Sets the exp contain stmt.
     */
    void setExpContainStmt();
}
