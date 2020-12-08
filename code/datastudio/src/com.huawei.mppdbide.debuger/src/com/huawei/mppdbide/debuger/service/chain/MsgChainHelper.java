/**
 * 
 */
package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.service.DebugService;

/**
 * @author z00588921
 *
 */
public class MsgChainHelper {
    private IMsgChain sqlMsgChain = null;
    private IMsgChain eventChain = null;
    public MsgChainHelper(DebugService debugService) {
        sqlMsgChain = new PrepareMsgChian(debugService);
        sqlMsgChain.setNext(new ServerPortMsgChain(debugService));
        
        eventChain = new ServerExitEventChain(debugService);
    }

    public void handleSqlMsg(Event event) {
        sqlMsgChain.handleMsg(event);
    }
    
    public void handleEventMsg(Event event) {
        eventChain.handleMsg(event);
    }
}
