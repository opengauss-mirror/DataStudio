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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * Title: TMergeASTNode
 *
 * @since 3.0.0
 */
public class TMergeWhenASTNode extends TBasicASTNode {

    private TExpression whenMatch = null;

    private TExpression matchDML = null;

    private TExpression whenNotMatch = null;

    private TExpression insertDML = null;

    public TExpression getWhenMatch() {
        return whenMatch;
    }

    public void setWhenMatch(TExpression whenMatch) {
        this.whenMatch = whenMatch;
        setPreviousObject(this.whenMatch);
    }

    public TExpression getMatchDML() {
        return matchDML;
    }

    public void setMatchDML(TExpression matchDML) {
        this.matchDML = matchDML;
        setPreviousObject(this.matchDML);
    }

    public TExpression getWhenNotMatch() {
        return whenNotMatch;
    }

    public void setWhenNotMatch(TExpression whenNotMatch) {
        this.whenNotMatch = whenNotMatch;
        setPreviousObject(this.whenNotMatch);
    }

    public TExpression getInsertDML() {
        return insertDML;
    }

    public void setInsertDML(TExpression insertDML) {
        this.insertDML = insertDML;
        setPreviousObject(this.insertDML);
    }

}
