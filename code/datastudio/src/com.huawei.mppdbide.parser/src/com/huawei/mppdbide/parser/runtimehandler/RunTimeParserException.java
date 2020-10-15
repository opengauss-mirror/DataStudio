/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.runtimehandler;

/**
 * Title: class Description: The Class RunTimeParserException. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class RunTimeParserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new run time parser exception.
     *
     * @param string the string
     */
    public RunTimeParserException(String string) {
        super(string);
    }

}
