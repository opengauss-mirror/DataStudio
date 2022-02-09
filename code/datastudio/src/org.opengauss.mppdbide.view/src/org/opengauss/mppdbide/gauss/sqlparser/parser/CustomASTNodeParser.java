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

package org.opengauss.mppdbide.gauss.sqlparser.parser;

import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TCTEExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * 
 * Title: CustomASTNodeParser
 *
 * @since 3.0.0
 */
public class CustomASTNodeParser extends CTEASTNodeParser {

    /**
     * Gets the t expression.
     *
     * @return the t expression
     */
    protected TExpression getTExpression() {
        return new TCTEExpression();
    }

}
