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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.insert;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TInsertValuesNodeItem
 *
 * @since 3.0.0
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
