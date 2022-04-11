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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Title: TParseTreeNodeList
 *
 * @since 3.0.0
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
