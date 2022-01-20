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

package com.huawei.mppdbide.gauss.sqlparser.parser.delete.ast;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.FromItemListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.delete.TDeleteUsingASTNode;

/**
 * 
 * Title: DeleteUsingASTNodeParser
 *
 * @since 3.0.0
 */
public class DeleteUsingASTNodeParser extends BasicASTNodeParser {

    @Override
    public TDeleteUsingASTNode getASTNodeBean() {
        return new TDeleteUsingASTNode();
    }

    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_USING;
    }

    @Override
    public AbstractNodeListParser getNodeListParser() {
        return new FromItemListParser(getKeywordList());
    }
}
