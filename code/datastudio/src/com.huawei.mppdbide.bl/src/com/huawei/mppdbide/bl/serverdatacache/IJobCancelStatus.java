/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IJobCancelStatus.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IJobCancelStatus {

    /**
     * Sets the cancel.
     *
     * @param isCancel the new cancel
     */
    void setCancel(boolean isCancel);

    /**
     * Gets the cancel.
     *
     * @return the cancel
     */
    boolean getCancel();

}
