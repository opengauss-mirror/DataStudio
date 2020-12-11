/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.DebugService;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ServerExitEventChain for use
 * Description: 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-08]
 * @since 2020-12-08
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
