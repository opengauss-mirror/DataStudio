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

package org.opengauss.mppdbide.bl.executor;

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.UnknownException;

/**
 * 
 * Title: ExecutorWrapper
 * 
 * Description:ExecutorWrapper
 * 
 * @since 3.0.0
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
