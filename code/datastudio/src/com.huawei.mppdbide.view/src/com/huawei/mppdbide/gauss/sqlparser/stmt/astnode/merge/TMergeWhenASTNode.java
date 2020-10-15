/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * Title: TMergeASTNode Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author s72444
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
