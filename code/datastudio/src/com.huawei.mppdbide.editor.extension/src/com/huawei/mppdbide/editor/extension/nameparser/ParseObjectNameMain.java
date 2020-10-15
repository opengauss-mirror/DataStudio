/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.editor.extension.nameparser;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.huawei.mppdbide.parser.grammar.PostgresLexer;
import com.huawei.mppdbide.parser.grammar.PostgresParser;
import com.huawei.mppdbide.parser.runtimehandler.RuntimeErrorStrategyManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class ParseObjectNameMain.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
