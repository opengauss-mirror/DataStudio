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
 * Title: TStartWithASTNode
 *
 * @since 3.0.0
 */
public class TStartWithASTNode extends TBasicASTNode {
    private TSqlNode with = null;

    /**
     * Gets the start.
     *
     * @return the start
     */
    public TSqlNode getStart() {
        return getKeywordNode();
    }

    /**
     * Gets the with.
     *
     * @return the with
     */
    public TSqlNode getWith() {
        return with;
    }

    /**
     * Sets the with.
     *
     * @param with the new with
     */
    public void setWith(TSqlNode with) {
        this.with = with;
        setPreviousObject(this.with);
    }
}
