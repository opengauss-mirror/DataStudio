/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;

/**
 * 
 * Title: TFullStmt
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
