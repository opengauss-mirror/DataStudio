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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TOrderByASTNode
 *
 * @since 3.0.0
 */
public class TOrderByASTNode extends TBasicASTNode {

    private TSqlNode SIBLINGS = null;

    private TSqlNode by = null;

    public TSqlNode getSIBLINGS() {
        return SIBLINGS;
    }

    public void setSIBLINGS(TSqlNode sIBLINGS) {
        SIBLINGS = sIBLINGS;
        setPreviousObject(this.SIBLINGS);
    }

    public TSqlNode getBy() {
        return by;
    }

    public void setBy(TSqlNode by) {
        this.by = by;
        setPreviousObject(this.by);
    }

}
