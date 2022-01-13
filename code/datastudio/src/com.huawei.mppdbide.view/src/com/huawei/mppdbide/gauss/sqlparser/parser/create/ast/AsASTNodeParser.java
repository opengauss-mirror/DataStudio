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

package com.huawei.mppdbide.gauss.sqlparser.parser.create.ast;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.begin.nodelist.DeclareItemListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TAsASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: AsASTNodeParser
 *
 * @since 3.0.0
 */
public class AsASTNodeParser extends AbstractASTNodeParser<TAsASTNode> {

    @Override
    public TAsASTNode prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator) {

        TAsASTNode asAstNode = new TAsASTNode();

        TSqlNode lLanguage = ParserUtils.handleToken(listIterator, "AS");
        if (null != lLanguage) {
            asAstNode.setKeywordNode(lLanguage);
        }

        lLanguage = ParserUtils.handleToken(listIterator, "IS");
        if (null != lLanguage) {
            asAstNode.setKeywordNode(lLanguage);
        }

        asAstNode.setStratAs(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_DOUBLE_DOLLER));

        Set<String> newKeyList = new HashSet<String>(getKeywordList());
        newKeyList.add(SQLFoldingConstants.SQL_DOUBLE_DOLLER);
        AbstractNodeListParser lFromItemListParser = new DeclareItemListParser(newKeyList);

        NodeListParserConverter.handleSelectList(listIterator, lFromItemListParser);
        TParseTreeNodeList<?> itemList = lFromItemListParser.getItemList();

        asAstNode.setItemList(itemList);

        asAstNode.setEndAs(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_DOUBLE_DOLLER));

        return asAstNode;
    }

}
