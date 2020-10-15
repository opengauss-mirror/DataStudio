/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IUIWorkerJobNotifier.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IUIWorkerJobNotifier {

    /**
     * This method will be called to notify a worker, which is waiting to obtain
     * SQL terminal connection
     *
     * @param notify the new notified
     */
    public void setNotified(boolean notify);

    /**
     * This method will be called to cancel a worker, which is waiting to obtain
     * SQL terminal connection
     *
     * @param cancel the new cancelled
     */
    public void setCancelled(boolean cancel);
}
