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

package org.opengauss.mppdbide.gauss.sqlparser.parser.merge.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.condition.ConditionBreakIf;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeWhenASTNode;

/**
 * Title: WithASTNodeParser
 *
 * @since 3.0.0
 */
public class MergeWhenMatchedASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TMergeWhenASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_WHEN;
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TMergeWhenASTNode orderByAstNode = (TMergeWhenASTNode) fromAstNode;

        ConditionBreakIf conditionIfWhen = new ConditionBreakIf() {
            @Override
            public boolean isBreakCondition(ISQLTokenData next, ListIterator<ISQLTokenData> listIterator) {
                if (null != next.getSubTokenBean()) {
                    return true;
                }
                return false;
            }
        };

        orderByAstNode.setWhenMatch(NodeExpressionConverter.parseAndGetExpression(listIterator, null, conditionIfWhen));

        Set<String> endTableName = new HashSet<String>(Arrays.asList("when", "log"));

        orderByAstNode.setMatchDML(NodeExpressionConverter.parseAndGetExpression(listIterator, endTableName));

        orderByAstNode
                .setWhenNotMatch(NodeExpressionConverter.parseAndGetExpression(listIterator, null, conditionIfWhen));

        Set<String> insertDmlExpression = new HashSet<String>(Arrays.asList("log"));

        orderByAstNode.setInsertDML(NodeExpressionConverter.parseAndGetExpression(listIterator, insertDmlExpression));

    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        return null;
    }

}
