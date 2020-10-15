/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IConnectionProfile.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IConnectionProfile {

    /**
     * Gets the server connection info.
     *
     * @return the server connection info
     */
    IServerConnectionInfo getServerConnectionInfo();

}
