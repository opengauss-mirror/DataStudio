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
 * Title: TSelectASTNode
 *
 * @since 3.0.0
 */
public class TSelectASTNode extends TBasicASTNode {

    private TSqlNode hintInfo = null;

    private TSqlNode SQL_CALC_FOUND_ROWS = null;

    private TSqlNode distinct = null;

    /**
     * Gets the hint info.
     *
     * @return the hint info
     */
    public TSqlNode getHintInfo() {
        return hintInfo;
    }

    /**
     * Sets the hint info.
     *
     * @param hintInfo the new hint info
     */
    public void setHintInfo(TSqlNode hintInfo) {
        this.hintInfo = hintInfo;
        setPreviousObject(this.hintInfo);
    }

    /**
     * Gets the sql calc found rows.
     *
     * @return the sql calc found rows
     */
    public TSqlNode getSqlCalcFoundRows() {
        return SQL_CALC_FOUND_ROWS;
    }

    /**
     * Sets the sql calc found rows.
     *
     * @param sQL_CALC_FOUND_ROWS the new sql calc found rows
     */
    public void setSqlCalcFoundRows(TSqlNode sQL_CALC_FOUND_ROWS) {
        SQL_CALC_FOUND_ROWS = sQL_CALC_FOUND_ROWS;
        setPreviousObject(this.SQL_CALC_FOUND_ROWS);
    }

    /**
     * Gets the distinct.
     *
     * @return the distinct
     */
    public TSqlNode getDistinct() {
        return distinct;
    }

    /**
     * Sets the distinct.
     *
     * @param distinct the new distinct
     */
    public void setDistinct(TSqlNode distinct) {
        this.distinct = distinct;
        setPreviousObject(this.distinct);
    }

}
