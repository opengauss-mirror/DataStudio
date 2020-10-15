/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class JobCancelStatus.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class JobCancelStatus implements IJobCancelStatus {

    private boolean isCancelled;

    @Override
    public void setCancel(boolean isCancel) {
        isCancelled = isCancel;
    }

    @Override
    public boolean getCancel() {
        return isCancelled;
    }

}
