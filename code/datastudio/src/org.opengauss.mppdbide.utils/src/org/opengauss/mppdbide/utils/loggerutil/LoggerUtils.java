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

package org.opengauss.mppdbide.utils.loggerutil;

import org.opengauss.mppdbide.utils.loggerif.ErrorLogWriterIf;

/**
 * 
 * Title: class
 * 
 * Description: The Class LoggerUtils.
 *
 * @since 3.0.0
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
