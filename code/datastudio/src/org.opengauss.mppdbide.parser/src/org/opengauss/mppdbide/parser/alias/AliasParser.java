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

package org.opengauss.mppdbide.parser.alias;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.opengauss.mppdbide.bl.queryparser.IParseContextGetter;
import org.opengauss.mppdbide.bl.queryparser.ParseContext;
import org.opengauss.mppdbide.parser.factory.PostgresParserFactory;
import org.opengauss.mppdbide.parser.runtimehandler.RunTimeParserException;
import org.opengauss.mppdbide.parser.runtimehandler.RuntimeErrorStrategyManager;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class Description: The Class AliasParser.
 *
 * @since 3.0.0
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
