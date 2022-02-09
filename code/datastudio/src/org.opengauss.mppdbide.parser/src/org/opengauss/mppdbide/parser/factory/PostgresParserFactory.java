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

package org.opengauss.mppdbide.parser.factory;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

import org.opengauss.mppdbide.parser.grammar.PostgresLexer;
import org.opengauss.mppdbide.parser.grammar.PostgresParser;

/**
 * Title: PostgresParserFactory Description:A factory for creating
 * PostgresParser objects.
 *
 * @since 3.0.0
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
