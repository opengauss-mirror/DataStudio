/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.select.ast;

import java.util.Arrays;
import java.util.ListIterator;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TOrderByASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.TSelectASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: SelectASTNodeParser
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
public class SelectASTNodeParser extends AbstractASTNodeParser<TSelectASTNode> {

    /**
     * Prepare AST stmt object.
     *
     * @param listIterator the list iterator
     * @return the t select AST node
     */
    public TSelectASTNode prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator) {

        TSelectASTNode selectAstNode = new TSelectASTNode();

        TSqlNode lSelect = ParserUtils.handleToken(listIterator, "SELECT", false);
        selectAstNode.setKeywordNode(lSelect);

        if (listIterator.hasNext()) {
            ISQLTokenData sqlTokenData = listIterator.next();

            if (null == sqlTokenData.getSubTokenBean()
                    && sqlTokenData.getToken().getData() == ISQLSyntax.SQL_MULTILINE_COMMENT) {
                TSqlNode lSqlHint = new TSqlNode();
                lSqlHint.setNodeText(sqlTokenData.getTokenStr());
                ParserUtils.addCommentsR(listIterator, lSqlHint);
                selectAstNode.setHintInfo(lSqlHint);
            } else {
                listIterator.previous();
            }
        } else {
            ParserUtils.addCommentsR(listIterator, lSelect);
        }

        // check for the SQL_CALC_FOUND_ROWS

        TSqlNode sqlCalcRows = ParserUtils.handleToken(listIterator, "SQL_CALC_FOUND_ROWS");
        if (null != sqlCalcRows) {
            selectAstNode.setSqlCalcFoundRows(sqlCalcRows);
        }

        // check for the SQL_CALC_FOUND_ROWS
        TSqlNode selectDistinct = ParserUtils.handleToken(listIterator, Arrays.asList("distinct", "all"));
        if (null != selectDistinct) {
            selectAstNode.setDistinct(selectDistinct);
        }

        // code to find the columns till found the from cluase, select
        // expression

        SelectResultListParser lSelectResultListParser = new SelectResultListParser(getKeywordList());

        NodeListParserConverter.handleSelectList(listIterator, lSelectResultListParser);

        TParseTreeNodeList<?> handleSelectList = lSelectResultListParser.getItemList();
        if (null != handleSelectList) {
            selectAstNode.setItemList(handleSelectList);
        }

        return selectAstNode;

    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TOrderByASTNode orderByAstNode = (TOrderByASTNode) fromAstNode;

        TSqlNode lSiblings = ParserUtils.handleToken(listIterator, "SIBLINGS");
        if (lSiblings != null) {
            orderByAstNode.setSIBLINGS(lSiblings);
        }

        TSqlNode lBy = ParserUtils.handleToken(listIterator, "BY");
        if (lSiblings != null) {
            orderByAstNode.setBy(lBy);
        }

    }

}
