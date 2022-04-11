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

package org.opengauss.mppdbide.editor.extension.nameparser;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.opengauss.mppdbide.parser.grammar.PostgresLexer;
import org.opengauss.mppdbide.parser.grammar.PostgresParser;
import org.opengauss.mppdbide.parser.runtimehandler.RuntimeErrorStrategyManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class ParseObjectNameMain.
 *
 * @since 3.0.0
 */
public class ParseObjectNameMain {
    ParserObjectNameListener parseListener = new ParserObjectNameListener();

    /**
     * Parsename.
     *
     * @param sqlquery the sqlquery
     */
    public void parsename(String sqlquery) {
        if (null != sqlquery) {
            ANTLRInputStream input = new ANTLRInputStream(sqlquery);
            PostgresLexer lexer = new PostgresLexer(input);
            lexer.addErrorListener(ObjectNameParseErrorListener.INSTANCE);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PostgresParser parser = new PostgresParser(tokens);
            parser.setErrorHandler(RuntimeErrorStrategyManager.getRuntimeErrorhandler());
            parser.removeErrorListeners();
            parser.addErrorListener(ObjectNameParseErrorListener.INSTANCE);
            ParseTree tree = parser.functionStmt();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(parseListener, tree);
        }
    }

    /**
     * Gets the object type.
     *
     * @return the object type
     */
    public String getObjectType() {
        return parseListener.getObjectName()[0];
    }

    /**
     * Gets the object name.
     *
     * @return the object name
     */
    public String getObjectName() {
        return parseListener.getObjectName()[1];
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public String getSchemaName() {
        return parseListener.getSchemaName();
    }

    /**
     * Gets the func name.
     *
     * @return the func name
     */
    public String getFuncName() {
        return parseListener.getFuncName();
    }

    /**
     * Gets the args.
     *
     * @return the args
     */
    public List<String[]> getArgs() {
        return parseListener.getArguements();
    }

    /**
     * Gets the ret type.
     *
     * @return the ret type
     */
    public String getRetType() {
        return parseListener.getreturnType();
    }

}
