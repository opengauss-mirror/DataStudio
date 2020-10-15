/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.loop.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.FullStatementConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.loop.TLoopASTNode;

/**
 * 
 * Title: LoopStmtASTNodeParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
 */
public class LoopStmtASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TLoopASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_LOOP;
    }

    /**
     * Prepare AST after list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTAfterListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TLoopASTNode orderByAstNode = (TLoopASTNode) fromAstNode;

        Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END));

        orderByAstNode.setFullStmt(FullStatementConverter.parseAndGetFullStmt(listIterator, lineBreakSet));

        // code to parse all the statements

        Set<String> endLineBreakList = new HashSet<String>();

        orderByAstNode.setEndLoop(NodeExpressionConverter.parseAndGetExpression(listIterator, endLineBreakList));

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