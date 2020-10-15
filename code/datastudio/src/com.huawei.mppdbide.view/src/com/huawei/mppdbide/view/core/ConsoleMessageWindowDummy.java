/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConsoleMessageWindowDummy.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConsoleMessageWindowDummy extends ConsoleMessageWindow {

    /**
     * Log msgs on console window.
     *
     * @param logMsg the log msg
     */
    public void logMsgsOnConsoleWindowDummy(String logMsg) {
        MPPDBIDELoggerUtility.none(logMsg);
    }

    /**
     * Log error.
     *
     * @param logMsg the log msg
     */
    public void logError(String logMsg) {
        MPPDBIDELoggerUtility.none(logMsg);
    }

}
