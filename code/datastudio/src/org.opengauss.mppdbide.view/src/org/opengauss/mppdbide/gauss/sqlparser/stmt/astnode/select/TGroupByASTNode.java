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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TGroupByASTNode
 *
 * @since 3.0.0
 */
public class TGroupByASTNode extends TBasicASTNode {
    private TSqlNode by = null;

    /**
     * Gets the group.
     *
     * @return the group
     */
    public TSqlNode getGroup() {
        return getKeywordNode();
    }

    /**
     * Gets the by.
     *
     * @return the by
     */
    public TSqlNode getBy() {
        return by;
    }

    /**
     * Sets the by.
     *
     * @param by the new by
     */
    public void setBy(TSqlNode by) {
        this.by = by;
        setPreviousObject(this.by);
    }

}
