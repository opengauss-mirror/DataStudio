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

package org.opengauss.mppdbide.gauss.sqlparser.parser.ast;

import java.util.ListIterator;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ParserUtils;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: BasicASTNodeParser
 *
 * @since 3.0.0
 */
public abstract class BasicASTNodeParser extends AbstractASTNodeParser<TBasicASTNode> {

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    public abstract TBasicASTNode getASTNodeBean();

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    public abstract String getKeywordTokenStr();

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    public abstract AbstractNodeListParser getNodeListParser();

    /**
     * Prepare AST stmt object.
     *
     * @param listIterator the list iterator
     * @return the t basic AST node
     */
    public TBasicASTNode prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator) {
        TBasicASTNode fromAstNode = getASTNodeBean();

        TSqlNode lFrom = getKeywordToken(listIterator);
        if (lFrom == null) {
            return null;
        }
        fromAstNode.setKeywordNode(lFrom);

        //
        prepareASTOtherStmtObject(listIterator, fromAstNode);

        prepareASTListItem(listIterator, fromAstNode);

        prepareASTAfterListItem(listIterator, fromAstNode);

        return fromAstNode;
    }

    /**
     * Gets the keyword token.
     *
     * @param listIterator the list iterator
     * @return the keyword token
     */
    protected TSqlNode getKeywordToken(ListIterator<ISQLTokenData> listIterator) {
        return ParserUtils.handleToken(listIterator, getKeywordTokenStr());
    }

    /**
     * Prepare AST after list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTAfterListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

    }

    /**
     * Prepare AST list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TParseTreeNodeList<?> handleFromList = handleFromList(listIterator);

        if (null != handleFromList) {
            fromAstNode.setItemList(handleFromList);
        }
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

    }

    /**
     * Handle from list.
     *
     * @param listIterator the list iterator
     * @return the t parse tree node list
     */
    protected TParseTreeNodeList<?> handleFromList(ListIterator<ISQLTokenData> listIterator) {
        AbstractNodeListParser lFromItemListParser = getNodeListParser();

        if (null == lFromItemListParser) {
            return null;
        }
        NodeListParserConverter.handleSelectList(listIterator, lFromItemListParser);
        TParseTreeNodeList<?> itemList = lFromItemListParser.getItemList();
        return itemList;
    }
}
