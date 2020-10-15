/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.alias;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.huawei.mppdbide.parser.grammar.PostgresParser;

/**
 * Title: AliasParserErrorListener Description:The listener interface for
 * receiving aliasParserError events. The class that is interested in processing
 * a aliasParserError event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addAliasParserErrorListener<code> method. When the aliasParserError
 * event occurs, that object's appropriate method is invoked. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public class AliasParserErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException exception) throws NoViableAltException {
        if (false == recognizer instanceof Parser) {
            return;
        }
        Parser parser = (Parser) recognizer;

        ParserRuleContext parent = parser.getRuleContext().getParent();

        if (parser.getRuleContext() instanceof PostgresParser.Alias_clauseContext) {
            while ((parent != null) && (parent.getRuleContext() instanceof PostgresParser.SelectStmtContext == false)) {
                parent = parent.getParent();
            }

            if (parent != null) {
                parent.exception = exception;
            }
        }
    }
}
