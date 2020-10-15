/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.alias;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Title: LexerErrorListener Description:The listener interface for receiving
 * lexerError events. The class that is interested in processing a lexerError
 * event implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>addLexerErrorListener<code> method. When the lexerError event occurs,
 * that object's appropriate method is invoked. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public class LexerErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) throws NoViableAltException {
        /*
         * Ignoring syntax error because, query is incomplete (and hence can
         * have invalid tokens) while parsing to extract alias to table name map
         */
    }
}
