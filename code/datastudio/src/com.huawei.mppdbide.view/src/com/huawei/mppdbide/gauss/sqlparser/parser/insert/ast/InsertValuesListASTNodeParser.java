/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.insert.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.insert.nodelist.InsertValuesListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertValuesASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * 
 * Title: InsertValuesListASTNodeParser
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
public class InsertValuesListASTNodeParser extends BasicASTNodeParser {

    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TInsertValuesASTNode();
    }

    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_VALUES;
    }

    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> asList = new HashSet<>(Arrays.asList(";"));
        asList.addAll(getKeywordList());
        return new InsertValuesListParser(asList);
    }

    /**
     * Prepare AST list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        if (listIterator.hasNext()) {
            ISQLTokenData next = listIterator.next();
            listIterator.previous();
            if (null != next && !"(".equals(next.getTokenStr())) {
                throw new GaussDBSQLParserException("Unable to parse the statement");
            }
        }
        TParseTreeNodeList<?> handleFromList = handleFromList(listIterator);
        if (null != handleFromList) {
            fromAstNode.setItemList(handleFromList);
        }
    }

}
