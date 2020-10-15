/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.runtimehandler;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;

/**
 * Title: class Description: The Class RunTimeExceptionCheckErrorStrategy.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class RunTimeExceptionCheckErrorStrategy extends DefaultErrorStrategy {
    @Override
    public void sync(Parser recognizer) throws RecognitionException {
        if (Thread.interrupted()) {
            throw new RunTimeParserException("formatting interrupted");
        }
        super.sync(recognizer);
    }

}
