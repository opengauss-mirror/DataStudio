/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.ast;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.FromItemListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TFromASTNode;

/**
 * 
 * Title: FromASTNodeParser
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
public class FromASTNodeParser extends BasicASTNodeParser {

    @Override
    public TFromASTNode getASTNodeBean() {
        return new TFromASTNode();
    }

    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_FROM;
    }

    @Override
    public AbstractNodeListParser getNodeListParser() {
        return new FromItemListParser(getKeywordList());
    }
}
