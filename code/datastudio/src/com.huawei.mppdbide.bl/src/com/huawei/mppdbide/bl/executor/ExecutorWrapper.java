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
