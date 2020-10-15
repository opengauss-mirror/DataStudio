/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Title: TParseTreeNodeList
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
public class TParseTreeNodeList<E> extends TParseTreeNode {
    private List<E> resultList = new ArrayList<E>();

    private boolean expContainStmt = false;

    /**
     * Gets the result list.
     *
     * @return the result list
     */
    public List<E> getResultList() {
        return resultList;
    }

    /**
     * Sets the result list.
     *
     * @param resultList the new result list
     */
    public void setResultList(List<E> resultList) {
        this.resultList = resultList;
    }

    /**
     * Adds the result column.
     *
     * @param resultCol the result col
     */
    public void addResultColumn(E resultCol) {
        this.resultList.add(resultCol);
    }

    /**
     * Checks if is exp contain stmt.
     *
     * @return true, if is exp contain stmt
     */
    public boolean isExpContainStmt() {
        return expContainStmt;
    }

    /**
     * Sets the exp contain stmt.
     *
     * @param expContainStmt the new exp contain stmt
     */
    public void setExpContainStmt(boolean expContainStmt) {
        this.expContainStmt = expContainStmt;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return this;
    }

}
