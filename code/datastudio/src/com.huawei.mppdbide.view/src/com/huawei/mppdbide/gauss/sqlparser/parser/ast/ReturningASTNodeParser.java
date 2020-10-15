/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.ast;

import java.util.HashSet;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.common.TReturningASTNode;

/**
 * 
 * Title: ReturningASTNodeParser
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
public class ReturningASTNodeParser extends BasicASTNodeParser {

    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TReturningASTNode();
    }

    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_RETURNING;
    }

    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> asList = new HashSet<>();
        return new SelectResultListParser(asList);
    }

}
