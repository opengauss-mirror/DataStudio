/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.factory;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

import com.huawei.mppdbide.parser.grammar.PostgresLexer;
import com.huawei.mppdbide.parser.grammar.PostgresParser;

/**
 * Title: PostgresParserFactory Description:A factory for creating
 * PostgresParser objects. Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public abstract class PostgresParserFactory {

    /**
     * Gets the postgres parser.
     *
     * @param lexer the lexer
     * @return the postgres parser
     */
    public static Parser getPostgresParser(Lexer lexer) {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Parser parser = new PostgresParser(tokens);
        return parser;
    }

    /**
     * Gets the postgres lexer.
     *
     * @param query the query
     * @return the postgres lexer
     */
    public static Lexer getPostgresLexer(String query) {
        ANTLRInputStream input = new ANTLRInputStream(query);
        Lexer lexer = new PostgresLexer(input);
        return lexer;
    }

    /**
     * Gets the postgres parser all stmt.
     *
     * @param parser the parser
     * @return the postgres parser all stmt
     */
    public static ParseTree getPostgresParserAllStmt(Parser parser) {

        if (parser instanceof PostgresParser) {
            return ((PostgresParser) parser).allStmt();
        }

        return null;
    }

}
