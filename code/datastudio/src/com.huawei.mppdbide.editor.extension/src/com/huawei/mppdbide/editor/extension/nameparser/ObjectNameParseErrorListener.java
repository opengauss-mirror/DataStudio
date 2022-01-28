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

package com.huawei.mppdbide.editor.extension.nameparser;

import java.util.ArrayList;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Title: ObjectNameParseErrorListener
 * 
 * Description:The listener interface for receiving objectNameParseError events.
 * The class that is interested in processing a objectNameParseError event
 * implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>addObjectNameParseErrorListener<code> method. When the
 * objectNameParseError event occurs, that object's appropriate method is
 * invoked.
 * 
 * @since 3.0.0
 */
public class ObjectNameParseErrorListener extends BaseErrorListener {

    /**
     * The Constant INSTANCE.
     */
    public static final ObjectNameParseErrorListener INSTANCE = new ObjectNameParseErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognier, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        if (recognier instanceof Parser) {
            Parser parser = (Parser) recognier;

            ParserRuleContext ctx = ((Parser) recognier).getRuleContext();
            ArrayList<String> affectedClauses = new ArrayList<String>(5);
            // keep this as the last item
            affectedClauses.add("PostgresParser$Simple_selectContext");

            while (ctx != null) {
                String className = ctx.getClass().getName();
                int lastIndexOf = className.lastIndexOf(".");
                className = className.substring(lastIndexOf + 1);

                if (affectedClauses.contains(className)) {
                    break;
                }
                ctx = ctx.getParent();
            }

            if ((ctx != null) && (ctx.exception == null)) {
                NoViableAltException recognitionException = new NoViableAltException(parser, parser.getTokenStream(),
                        ((Parser) recognier).getCurrentToken(), ((Parser) recognier).getCurrentToken(), null,
                        parser.getRuleContext());
                ctx.exception = recognitionException;
            }
        }
    }
}
