/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.DebugService;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: PrepareMsgChian
 * 
 * Description: PrepareMsgChian to instance IMsgChain and deal with ON_SQL_MSG
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */
public class PrepareMsgChian extends IMsgChain {
    /**
     *  msg to match
     */
    public static final String PREPARE_SUCCESS = "Pldebugger is started successfully, you are";
    private DebugService debugService;

    public PrepareMsgChian(DebugService debugService) {
        super();
        this.debugService = debugService;
    }

    @Override
    public boolean matchMsg(Event event) {
        if (event.getMsg() == EventMessage.ON_SQL_MSG
                && event.getStringAddition().contains(PREPARE_SUCCESS)) {
            return true;
        }
        return false;
    }

    @Override
    public void disposeMsg(Event event) {
        String msg = event.getStringAddition();
        if (msg.contains("SERVER")) {
            debugService.getServerDebugState().prepared();
        } else if (msg.contains("CLIENT")) {
            debugService.getClientDebugState().prepared();
        } else {
            MPPDBIDELoggerUtility.debug("can\'t run here!");
        }
    }

}
