/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TInsertValuesNodeItem
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
public class TInsertValuesNodeItem extends TAbstractListItem {

    private TSqlNode startInsertBracket = null;

    private TParseTreeNodeList<?> valueItemList = null;

    private TSqlNode endInsertBracket = null;

    private TSqlNode seperator = null;

    /**
     * Gets the start insert bracket.
     *
     * @return the start insert bracket
     */
    public TSqlNode getStartInsertBracket() {
        return startInsertBracket;
    }

    /**
     * Sets the start insert bracket.
     *
     * @param startBracket the new start insert bracket
     */
    public void setStartInsertBracket(TSqlNode startBracket) {
        this.startInsertBracket = startBracket;
        setPreviousObject(this.startInsertBracket);
    }

    /**
     * Gets the insert end bracket.
     *
     * @return the insert end bracket
     */
    public TSqlNode getInsertEndBracket() {
        return endInsertBracket;
    }

    /**
     * Sets the insert end bracket.
     *
     * @param endBracket the new insert end bracket
     */
    public void setInsertEndBracket(TSqlNode endBracket) {
        this.endInsertBracket = endBracket;
        setPreviousObject(this.endInsertBracket);
    }

    /**
     * Gets the value item list.
     *
     * @return the value item list
     */
    public TParseTreeNodeList<?> getValueItemList() {
        return valueItemList;
    }

    /**
     * Sets the value item list.
     *
     * @param valueItemList the new value item list
     */
    public void setValueItemList(TParseTreeNodeList<?> valueItemList) {
        this.valueItemList = valueItemList;
        setPreviousObject(this.valueItemList);
    }

    /**
     * Gets the seperator.
     *
     * @return the seperator
     */
    public TSqlNode getSeperator() {
        return seperator;
    }

    /**
     * Sets the seperator.
     *
     * @param seperator the new seperator
     */
    public void setSeperator(TSqlNode seperator) {
        this.seperator = seperator;
        setPreviousObject(this.seperator);
    }

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        return this.getAutoStartBean();
    }

}
