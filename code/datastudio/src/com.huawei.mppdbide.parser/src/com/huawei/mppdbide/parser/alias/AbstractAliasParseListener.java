/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.alias;

import com.huawei.mppdbide.parser.grammar.PostgresParser;
import com.huawei.mppdbide.parser.grammar.PostgresParserBaseListener;

/**
 * Title: AbstractAliasParseListener Description:The listener interface for
 * receiving abstractAliasParse events. The class that is interested in
 * processing a abstractAliasParse event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addAbstractAliasParseListener<code> method. When the
 * abstractAliasParse event occurs, that object's appropriate method is invoked.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public abstract class AbstractAliasParseListener extends PostgresParserBaseListener {
    boolean isException = false;

    @Override
    public void enterSelectStmt(PostgresParser.SelectStmtContext ctx) {
        if (ctx.exception != null) {
            isException = true;
        }
    }
}
