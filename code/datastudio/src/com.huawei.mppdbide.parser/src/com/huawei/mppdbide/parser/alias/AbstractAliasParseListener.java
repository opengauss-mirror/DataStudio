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
 *
 * @since 3.0.0
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
