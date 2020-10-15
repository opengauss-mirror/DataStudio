/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.nodelist;

import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserFactory;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.condition.ConditionBreakIf;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;

/**
 * 
 * Title: NodeExpressionConverter
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
public class NodeExpressionConverter {

    /**
     * Parses the and get expression.
     *
     * @param listIterator the list iterator
     * @param keywordList the keyword list
     * @return the t expression
     */
    public static TExpression parseAndGetExpression(ListIterator<ISQLTokenData> listIterator, Set<String> keywordList) {
        return parseAndGetExpression(listIterator, keywordList, null);
    }

    /**
     * Parses the and get expression break on subobject.
     *
     * @param listIterator the list iterator
     * @param keywordList the keyword list
     * @return the t expression
     */
    public static TExpression parseAndGetExpressionBreakOnSubobject(ListIterator<ISQLTokenData> listIterator,
            Set<String> keywordList) {
        return parseAndGetExpression(listIterator, keywordList, new ConditionBreakIf() {
            @Override
            public boolean isBreakCondition(ISQLTokenData next, ListIterator<ISQLTokenData> listIterator) {
                if (null != next.getSubTokenBean()) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Parses the and get expression.
     *
     * @param listIterator the list iterator
     * @param keywordList the keyword list
     * @param conditionIf the condition if
     * @return the t expression
     */
    public static TExpression parseAndGetExpression(ListIterator<ISQLTokenData> listIterator, Set<String> keywordList,
            ConditionBreakIf conditionIf) {
        TExpression expression = null;

        while (listIterator.hasNext()) {
            ISQLTokenData next = listIterator.next();
            String tokenStr = next.getTokenStr();

            if (null != conditionIf && conditionIf.isBreakCondition(next, listIterator)) {
                listIterator.previous();
                break;
            }

            if (null != next.getSubTokenBean()) {
                // sub query/stmt handle
                TCustomSqlStatement customSqlStmt = ParserFactory.getCustomSqlStmt(next.getSubTokenBean());

                if (null == expression) {
                    expression = new TExpression();
                }
                ParserUtils.addCustomStmtToExpression(expression, customSqlStmt, listIterator);
                continue;
            }

            // if empty char then continue;
            if (ParserUtils.isTokenEmpty(next)) {
                continue;
            }

            if (keywordList != null && keywordList.contains(tokenStr.toLowerCase())) {
                listIterator.previous();
                break;
            }

            TExpressionNode expNode = new TExpressionNode();
            expNode.getExpNode().setNodeText(next.getTokenStr());

            ParserUtils.addCommentsR(listIterator, expNode.getExpNode());
            if (null == expression) {
                expression = new TExpression();
            }
            expression.addExpressionNode(expNode);

        }

        return expression;
    }
}
