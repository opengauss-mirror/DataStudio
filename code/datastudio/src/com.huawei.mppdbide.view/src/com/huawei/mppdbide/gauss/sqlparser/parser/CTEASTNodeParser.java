/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
