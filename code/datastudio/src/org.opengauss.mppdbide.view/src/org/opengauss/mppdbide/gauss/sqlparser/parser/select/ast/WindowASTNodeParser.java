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

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import org.opengauss.mppdbide.gauss.sqlparser.parser.with.nodelist.WithItemListParser;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.select.TWindowASTNode;

/**
 * 
 * Title: WindowASTNodeParser
 * 
 * Description: The Class WindowASTNodeParser.
 *
 * @since 3.0.0
 */
public class WindowASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_WINDOW;
    }

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TWindowASTNode();
    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        return new WithItemListParser(getKeywordList());
    }
}