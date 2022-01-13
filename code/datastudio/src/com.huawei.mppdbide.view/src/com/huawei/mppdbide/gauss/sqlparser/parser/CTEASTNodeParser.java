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

package com.huawei.mppdbide.gauss.sqlparser.parser;

import java.util.ListIterator;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TCTEASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;

/**
 * 
 * Title: CTEASTNodeParser
 *
 * @since 3.0.0
 */
public class CTEASTNodeParser extends AbstractASTNodeParser<TCTEASTNode> {

    /**
     * Prepare AST stmt object.
     *
     * @param listIterator the list iterator
     * @return the TCTEAST node
     */
    @Override
    public TCTEASTNode prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator) {

        TCTEASTNode orderByAstNode = new TCTEASTNode();

        TExpression stmtExpression = null;

        while (listIterator.hasNext()) {

            ISQLTokenData next = listIterator.next();

            if (null == stmtExpression) {
                stmtExpression = getTExpression();
                stmtExpression.setDirectExpression(true);
                orderByAstNode.setStmtExpression(stmtExpression);
            }

            if (null != next.getSubTokenBean()) {
                TCustomSqlStatement customSqlStmt = ParserFactory.getCustomSqlStmt(next.getSubTokenBean());

                ParserUtils.addCustomStmtToExpression(stmtExpression, customSqlStmt, listIterator);
            } else {
                ParserUtils.addExpressionNode(stmtExpression, listIterator, next);
                // wrong sql format
            }

        }

        orderByAstNode.setStmtExpression(stmtExpression);

        return orderByAstNode;
    }

    /**
     * Gets the t expression.
     *
     * @return the t expression
     */
    protected TExpression getTExpression() {
        return new TExpression();
    }

}
