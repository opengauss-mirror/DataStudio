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

package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.DebugService;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: PrepareMsgChian
 * Description: PrepareMsgChian to instance IMsgChain and deal with ON_SQL_MSG
 *
 * @since 3.0.0
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
