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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * 
 * Title: TLaungageASTNode
 *
 * @since 3.0.0
 */
public class TLaungageASTNode extends TBasicASTNode {

    private TExpression languageExpression = null;
    private TExpression commonExpression = null;

    public TExpression getLanguageExpression() {
        return languageExpression;
    }

    public void setLanguageExpression(TExpression languageExpression) {
        this.languageExpression = languageExpression;
        setPreviousObject(this.languageExpression);
    }

    public TExpression getCommonExpression() {
        return commonExpression;
    }

    public void setCommonExpression(TExpression commonExpression) {
        this.commonExpression = commonExpression;
        setPreviousObject(this.commonExpression);
    }

}
