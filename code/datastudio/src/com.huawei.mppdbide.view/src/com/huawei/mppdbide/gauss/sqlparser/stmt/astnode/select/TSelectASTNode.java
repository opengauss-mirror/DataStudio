/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TSelectASTNode
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
