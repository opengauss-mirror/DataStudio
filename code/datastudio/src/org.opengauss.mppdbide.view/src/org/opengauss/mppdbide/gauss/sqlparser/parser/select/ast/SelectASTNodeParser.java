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

package org.opengauss.mppdbide.gauss.sqlparser.parser.select.ast;

import java.util.Arrays;
import java.util.ListIterator;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import org.opengauss.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserUtils;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import org.opengauss.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common.TOrderByASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.TSelectASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: SelectASTNodeParser
 *
 * @since 3.0.0
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
