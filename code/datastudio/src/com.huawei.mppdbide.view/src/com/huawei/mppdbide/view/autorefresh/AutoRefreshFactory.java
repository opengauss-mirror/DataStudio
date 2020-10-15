/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.autorefresh;

import java.util.HashSet;

import com.huawei.mppdbide.presentation.IExecutionContext;

/**
 * Title: AutoRefreshFactory
 * 
 * Description:AutoRefreshFactory
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author pWX759367
 * @version [DataStudio 6.5.1, 30-Aug-2019]
 * @since 30-Aug-2019
 */

public class AutoRefreshFactory {

    /**
     * Gets the auto refresh instance.
     *
     * @param listOfObjects the list of objects
     * @param executionContext the execution context
     * @return the auto refresh instance
     */
    public static AutoRefreshObject getAutoRefreshInstance(HashSet<Object> listOfObjects,
            IExecutionContext executionContext) {
        AutoRefreshObject autoRefreshObject;

        autoRefreshObject = new AutoRefreshOLAP(listOfObjects, executionContext);
        return autoRefreshObject;

    }
}
