/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.executor;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.UnknownException;

/**
 * 
 * Title: ExecutorWrapper
 * 
 * Description:ExecutorWrapper
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 15 Oct, 2019]
 * @since 15 Oct, 2019
 */
public interface ExecutorWrapper {

    /**
     * Connect to server.
     *
     * @param serverInfo the server info
     * @param iConnectionDriver the i connection driver
     * @throws UnknownException the unknown exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    void connectToServer(IServerConnectionInfo serverInfo, IConnectionDriver iConnectionDriver)
            throws UnknownException, MPPDBIDEException;

}
