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

import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

import com.huawei.mppdbide.bl.queryparser.ParseContext;
import com.huawei.mppdbide.parser.grammar.PostgresParser;
import com.huawei.mppdbide.parser.grammar.PostgresParser.Relation_exprContext;
import com.huawei.mppdbide.parser.grammar.PostgresParser.Table_refContext;

/**
 * Title: AliasParseListener Description:The listener interface for receiving
 * aliasParse events. The class that is interested in processing a aliasParse
 * event implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>addAliasParseListener<code> method. When the aliasParse event occurs,
 * that object's appropriate method is invoked.
 *
 * @since 3.0.0
 */
public class AliasParseListener extends AbstractAliasParseListener {
    private ParseContext aliasParseContext;

    /**
     * Instantiates a new alias parse listener.
     */
    AliasParseListener() {
        aliasParseContext = new ParseContext();
    }

    /**
     * check the alias clause to get the alias.
     *
     * @param ctx the ctx
     */
    @Override
    public void enterAlias_clause(PostgresParser.Alias_clauseContext ctx) {
        if (isException) {
            return;
        }

        ParserRuleContext tempCtx = (ParserRuleContext) ctx;
        ParserRuleContext parent = tempCtx.getParent();

        String aliasName;

        if (tempCtx.getChildCount() == 2) {
            aliasName = tempCtx.getChild(1).getText();
        } else {
            aliasName = tempCtx.getText();
        }

        if (parent instanceof Table_refContext) {
            ParseTree firstChild = parent.getChild(0);
            if (firstChild instanceof Relation_exprContext) {
                String tableName = firstChild.getText();
                addToAliasParseContext(aliasName, tableName);
            }
        }
    }

    @Override
    public void enterRelation_expr_opt_alias(PostgresParser.Relation_expr_opt_aliasContext ctx) {
        if (isException) {
            return;
        }

        ParserRuleContext tempCtx = (ParserRuleContext) ctx;

        int childCount = tempCtx.getChildCount();
        if (childCount > 1) {
            addToAliasParseContext(tempCtx.getChild(childCount - 1).getText(), tempCtx.getChild(0).getText());
        }
    }

    private void addToAliasParseContext(String aliasName, String tableName) {
        if (aliasParseContext.getAliasToTableNameMap().containsKey(aliasName) == false) {
            aliasParseContext.getAliasToTableNameMap().put(aliasName, new LinkedList<String>());
        }
        aliasParseContext.getAliasToTableNameMap().get(aliasName).add(tableName);
    }

    /**
     * Gets the alias parse context.
     *
     * @return the alias parse context
     */
    public ParseContext getAliasParseContext() {
        return aliasParseContext;
    }
}
