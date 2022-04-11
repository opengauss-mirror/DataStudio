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

import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * 
 * Title: AbstractNodeListParser
 *
 * @since 3.0.0
 */
public abstract class AbstractNodeListParser implements NodeListParser {

    /** 
     * The list break. 
     */
    protected Set<String> listBreak = null;

    /**
     * Instantiates a new abstract node list parser.
     *
     * @param lineBreakSet the line break set
     */
    public AbstractNodeListParser(Set<String> lineBreakSet) {
        this.listBreak = lineBreakSet;
    }

    /**
     * Gets the item list.
     *
     * @return the item list
     */
    public abstract TParseTreeNodeList<?> getItemList();

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
     * Checks if is list break with custom SQL.
     *
     * @param previousNotEmptyToken the previous not empty token
     * @param sqlStmtTokenListBean the sql stmt token list bean
     * @return true, if is list break with custom SQL
     */
    @Override
    public boolean isListBreakWithCustomSQL(String previousNotEmptyToken, SQLStmtTokenListBean sqlStmtTokenListBean) {
        return false;
    }

    /**
     * Sets the exp contain stmt.
     */
    public void setExpContainStmt() {
        getItemList().setExpContainStmt(true);
    }

}
