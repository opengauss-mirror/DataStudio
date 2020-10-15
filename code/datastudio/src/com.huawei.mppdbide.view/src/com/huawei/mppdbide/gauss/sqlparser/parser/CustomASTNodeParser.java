/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TCTEExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * 
 * Title: CustomASTNodeParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 04-Dec-2019]
 * @since 04-Dec-2019
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
