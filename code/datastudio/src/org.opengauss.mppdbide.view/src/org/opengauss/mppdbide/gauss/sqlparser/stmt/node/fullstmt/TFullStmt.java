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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;

/**
 * 
 * Title: TFullStmt
 *
 * @since 3.0.0
 */
public class TFullStmt extends TParseTreeNode implements StatementBeanIf {

    private List<TParseTreeNode> stmtList = new ArrayList<TParseTreeNode>();

    /**
     * Adds the stmt node.
     *
     * @param exp the exp
     */
    public void addStmtNode(TParseTreeNode exp) {
        this.stmtList.add(exp);
    }

    /**
     * Gets the stmt list.
     *
     * @return the stmt list
     */
    public List<TParseTreeNode> getStmtList() {
        return stmtList;
    }

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        return null;
    }

}
