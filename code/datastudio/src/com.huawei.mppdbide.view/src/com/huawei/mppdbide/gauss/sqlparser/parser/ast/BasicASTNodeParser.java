/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.ast;

import java.util.ListIterator;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: BasicASTNodeParser
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
