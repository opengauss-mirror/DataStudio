/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.loggerif;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ErrorLogWriterIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface ErrorLogWriterIf {

    /**
     * Error log.
     *
     * @param msg the msg
     */
    void errorLog(String msg);
}
