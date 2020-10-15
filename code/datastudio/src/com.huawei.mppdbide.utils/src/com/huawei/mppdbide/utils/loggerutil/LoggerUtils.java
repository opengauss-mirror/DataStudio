/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.loggerutil;

import com.huawei.mppdbide.utils.loggerif.ErrorLogWriterIf;

/**
 * 
 * Title: class
 * 
 * Description: The Class LoggerUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class LoggerUtils {
    private static ErrorLogWriterIf errorLogIf = null;

    /**
     * Sets the error log writer.
     *
     * @param lErrorLogWriter the new error log writer
     */
    public static void setErrorLogWriter(ErrorLogWriterIf lErrorLogWriter) {
        errorLogIf = lErrorLogWriter;
    }

    /**
     * Error.
     *
     * @param msg the msg
     */
    public static void error(String msg) {
        if (null != errorLogIf) {
            errorLogIf.errorLog(msg);
        }

    }

}
