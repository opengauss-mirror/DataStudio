/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.nodelist;

import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * 
 * Title: AbstractNodeListParser
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
