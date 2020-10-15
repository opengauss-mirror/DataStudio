/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.create.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TReturnASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: ReturnsASTNodeParser
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
public class ReturnsASTNodeParser extends AbstractASTNodeParser<TReturnASTNode> {

    @Override
    public TReturnASTNode prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator) {

        TReturnASTNode returnAstNode = new TReturnASTNode();

        TSqlNode lreturn = ParserUtils.handleToken(listIterator, "RETURNS");
        if (null != lreturn) {
            returnAstNode.setKeywordNode(lreturn);
        } else {
            lreturn = ParserUtils.handleToken(listIterator, "RETURN");
            returnAstNode.setKeywordNode(lreturn);
        }

        TSqlNode ltable = ParserUtils.handleToken(listIterator, "TABLE");
        returnAstNode.setTable(ltable);

        if (null != ltable) {

            // read the column list
            // ()
            TSqlNode startBracket = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_START);
            returnAstNode.setReturnStartBracket(startBracket);

            Set<String> asList = new HashSet<>(Arrays.asList(SQLFoldingConstants.SQL_BRACKET_END));
            asList.addAll(getKeywordList());

            AbstractNodeListParser lFromItemListParser = new SelectResultListParser(asList);

            NodeListParserConverter.handleSelectList(listIterator, lFromItemListParser);
            TParseTreeNodeList<?> itemList = lFromItemListParser.getItemList();

            returnAstNode.setReturnTableList(itemList);

            TSqlNode endBracket = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_END);
            returnAstNode.setReturnEndBracket(endBracket);

        } else {

            Set<String> endLineBreakList = new HashSet<String>(Arrays.asList("deterministic"));
            endLineBreakList.addAll(getKeywordList());
            endLineBreakList.addAll(ParserUtils.getCommonKeywordListForFunProc());
            returnAstNode
                    .setResultExpression(NodeExpressionConverter.parseAndGetExpression(listIterator, endLineBreakList));

            TSqlNode ldeterministic = ParserUtils.handleToken(listIterator, "DETERMINISTIC", false);
            if (null != ldeterministic) {
                returnAstNode.setDeterministic(ldeterministic);
            }

            // expression read the return rettype
        }

        returnAstNode
                .setCommonExpression(NodeExpressionConverter.parseAndGetExpression(listIterator, getKeywordList()));

        return returnAstNode;
    }

}
