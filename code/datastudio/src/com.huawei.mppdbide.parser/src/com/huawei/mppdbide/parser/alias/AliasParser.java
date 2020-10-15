/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.alias;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.huawei.mppdbide.bl.queryparser.IParseContextGetter;
import com.huawei.mppdbide.bl.queryparser.ParseContext;
import com.huawei.mppdbide.parser.factory.PostgresParserFactory;
import com.huawei.mppdbide.parser.runtimehandler.RunTimeParserException;
import com.huawei.mppdbide.parser.runtimehandler.RuntimeErrorStrategyManager;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class Description: The Class AliasParser. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AliasParser implements IParseContextGetter {
    private ParseContext aliases;

    private LexerErrorListener lexerErrlorListener;

    private AliasParserErrorListener parserErrlorListener;

    private ParseTreeWalker walker;

    /**
     * Instantiates a new alias parser.
     */
    public AliasParser() {
        aliases = null;
        lexerErrlorListener = new LexerErrorListener();
        parserErrlorListener = new AliasParserErrorListener();
        walker = new ParseTreeWalker();
    }

    /**
     * parse a query using antlr4
     * 
     * @param query - query to be parsed
     */
    public void parseQuery(String query) {

        Lexer lexer = PostgresParserFactory.getPostgresLexer(query);
        lexer.removeErrorListeners();
        lexer.addErrorListener(lexerErrlorListener);
        Parser parser = PostgresParserFactory.getPostgresParser(lexer);
        parser.setErrorHandler(RuntimeErrorStrategyManager.getRuntimeErrorhandler());
        parser.removeErrorListeners();
        parser.addErrorListener(parserErrlorListener);
        AliasParseListener parseListener = new AliasParseListener();

        try {
            ParseTree tree = PostgresParserFactory.getPostgresParserAllStmt(parser);
            walker.walk(parseListener, tree);
            aliases = parseListener.getAliasParseContext();
        } catch (Exception exception) {
            if (exception instanceof RunTimeParserException) {
                MPPDBIDELoggerUtility.error("Alias parser worker interrupted", exception);
                throw exception;
            } else {
                MPPDBIDELoggerUtility.error("Error while parsing", exception);
            }
        }
    }

    /**
     * getter method for the parsed information
     * 
     * @return - parse context for the query parsed
     */
    public ParseContext getParseContext() {
        return aliases;
    }
}
