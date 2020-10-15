/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.merge.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.condition.ConditionBreakIf;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeWhenASTNode;

/**
 * Title: WithASTNodeParser Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
