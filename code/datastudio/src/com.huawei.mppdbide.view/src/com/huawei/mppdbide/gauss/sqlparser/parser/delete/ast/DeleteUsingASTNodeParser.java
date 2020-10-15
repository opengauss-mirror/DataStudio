/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
