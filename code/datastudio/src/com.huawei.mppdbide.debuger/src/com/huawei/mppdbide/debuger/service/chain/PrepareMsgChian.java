/**
 * 
 */
package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.DebugService;

/**
 * @author z00588921
 *
 */
public class PrepareMsgChian extends IMsgChain {
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
        }
    }

}
