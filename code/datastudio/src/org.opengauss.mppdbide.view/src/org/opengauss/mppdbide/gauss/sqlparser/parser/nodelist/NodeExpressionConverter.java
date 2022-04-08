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

package org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist;

import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserFactory;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserUtils;
import org.opengauss.mppdbide.gauss.sqlparser.parser.condition.ConditionBreakIf;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;

/**
 * 
 * Title: NodeExpressionConverter
 *
 * @since 3.0.0
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
