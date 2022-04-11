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

package org.opengauss.mppdbide.debuger.service.chain;

import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.service.DebugService;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ServerExitEventChain for use
 *
 * @since 3.0.0
 */
public class ServerExitEventChain extends IMsgChain {
    private DebugService debugService;

    public ServerExitEventChain(DebugService debugService) {
        super();
        this.debugService = debugService;
    }

    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.ON_EXIT;
    }

    @Override
    protected void disposeMsg(Event event) {
        if (event.hasException()) {
            MPPDBIDELoggerUtility.debug("server exited with exception:" + event.getException().getMessage());
            debugService.updateServerWithException();
        } else {
            MPPDBIDELoggerUtility.debug("server exited normal, result = " + event.getAddition().orElse(""));
            debugService.updateServerWithResult(event.getAddition().orElse(""));
        }
    }

}
