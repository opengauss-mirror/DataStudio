/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.update;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TUpdateSetValuesBean
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
public class TUpdateSetValuesBean extends TParseTreeNode {

    private TSqlNode startUpdateBracket = null;

    private TParseTreeNodeList<?> valueItemList = null;

    private TSqlNode endUpdateBracket = null;

    /**
     * Gets the update start bracket.
     *
     * @return the update start bracket
     */
    public TSqlNode getUpdateStartBracket() {
        return startUpdateBracket;
    }

    /**
     * Sets the update start bracket.
     *
     * @param startBracket the new update start bracket
     */
    public void setUpdateStartBracket(TSqlNode startBracket) {
        this.startUpdateBracket = startBracket;
        setPreviousObject(this.startUpdateBracket);
    }

    /**
     * Gets the update end bracket.
     *
     * @return the update end bracket
     */
    public TSqlNode getUpdateEndBracket() {
        return endUpdateBracket;
    }

    /**
     * Sets the update end bracket.
     *
     * @param endBracket the new update end bracket
     */
    public void setUpdateEndBracket(TSqlNode endBracket) {
        this.endUpdateBracket = endBracket;
        setPreviousObject(this.endUpdateBracket);
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
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        return this.getAutoStartBean();
    }

}
