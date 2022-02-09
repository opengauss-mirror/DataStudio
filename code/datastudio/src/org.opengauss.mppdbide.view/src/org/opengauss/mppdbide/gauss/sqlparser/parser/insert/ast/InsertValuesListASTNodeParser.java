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

package org.opengauss.mppdbide.gauss.sqlparser.parser.insert.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.insert.nodelist.InsertValuesListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.insert.TInsertValuesASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * 
 * Title: InsertValuesListASTNodeParser
 *
 * @since 3.0.0
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
